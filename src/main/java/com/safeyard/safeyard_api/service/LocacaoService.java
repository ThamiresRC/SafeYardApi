package com.safeyard.safeyard_api.service;

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Locacao;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.model.Moto.StatusMoto;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocacaoService {

    private final LocacaoRepository repository;
    private final ClienteRepository clienteRepository;
    private final MotoRepository motoRepository;

    @Transactional
    @CacheEvict(
            value = { "locacoesFiltradas", "locacoesAtivas", "locacoesPorCliente", "locacoesCount", "motos", "motoById" },
            allEntries = true
    )
    public LocacaoDTO create(LocacaoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente nÃƒÂ£o encontrado"));

        Moto moto = motoRepository.findById(dto.motoId())
                .orElseThrow(() -> new EntityNotFoundException("Moto nÃƒÂ£o encontrada"));

        LocalDateTime saida = dto.dataSaida() != null ? dto.dataSaida() : LocalDateTime.now();

        LocalDateTime previsaoDevolucao = dto.dataDevolucao();
        if (previsaoDevolucao != null && previsaoDevolucao.isBefore(saida)) {
            throw new IllegalArgumentException("Data de devoluÃƒÂ§ÃƒÂ£o prevista nÃƒÂ£o pode ser anterior ÃƒÂ  saÃƒÂ­da.");
        }

        LocalDateTime fim = (previsaoDevolucao != null) ? previsaoDevolucao : saida.plusYears(100);
        if (repository.existsOverlapForMoto(moto.getId(), saida, fim)) {
            throw new IllegalArgumentException("JÃƒÂ¡ existe locaÃƒÂ§ÃƒÂ£o ativa/colidente para esta moto no perÃƒÂ­odo informado.");
        }

        String obsDevolucao = dto.condicaoDevolucao();
        if (previsaoDevolucao != null) {
            String tagPrev = " (DevoluÃƒÂ§ÃƒÂ£o prevista: " + previsaoDevolucao + ")";
            obsDevolucao = (obsDevolucao == null || obsDevolucao.isBlank())
                    ? tagPrev : (obsDevolucao + tagPrev);
        }

        Locacao locacao = Locacao.builder()
                .dataSaida(saida)
                .dataDevolucao(null)
                .condicaoEntrega(dto.condicaoEntrega())
                .condicaoDevolucao(obsDevolucao)
                .qrCode(dto.qrCode())
                .cliente(cliente)
                .moto(moto)
                .build();

        Locacao salvo = repository.save(locacao);

        moto.setStatus(StatusMoto.EM_USO);
        motoRepository.save(moto);

        return toDTO(salvo);
    }

    @Transactional
    @CacheEvict(
            value = { "locacoesFiltradas", "locacoesAtivas", "locacoesPorCliente", "locacoesCount", "motos", "motoById" },
            allEntries = true
    )
    public LocacaoDTO update(Long id, LocacaoDTO dto) {
        Locacao locacao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LocaÃƒÂ§ÃƒÂ£o nÃƒÂ£o encontrada"));

        if (dto.clienteId() != null && !dto.clienteId().equals(
                locacao.getCliente() != null ? locacao.getCliente().getId() : null)) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente nÃƒÂ£o encontrado"));
            locacao.setCliente(novoCliente);
        }

        Long motoIdAtual = locacao.getMoto() != null ? locacao.getMoto().getId() : null;
        if (dto.motoId() != null && !dto.motoId().equals(motoIdAtual)) {
            Moto novaMoto = motoRepository.findById(dto.motoId())
                    .orElseThrow(() -> new EntityNotFoundException("Moto nÃƒÂ£o encontrada"));

            LocalDateTime saida = dto.dataSaida() != null ? dto.dataSaida() : locacao.getDataSaida();
            LocalDateTime fim = (dto.dataDevolucao() != null ? dto.dataDevolucao()
                    : (locacao.getDataDevolucao() != null ? locacao.getDataDevolucao() : saida.plusYears(100)));

            if (repository.existsOverlapForMotoExcludingId(novaMoto.getId(), id, saida, fim)) {
                throw new IllegalArgumentException("JÃƒÂ¡ existe locaÃƒÂ§ÃƒÂ£o ativa/colidente para esta moto no perÃƒÂ­odo informado.");
            }

            if (motoIdAtual != null && !motoIdAtual.equals(novaMoto.getId())) {
                boolean temAtiva = repository.existsByMotoIdAndDataDevolucaoIsNull(motoIdAtual);
                if (!temAtiva) {
                    Moto antiga = motoRepository.findById(motoIdAtual).orElse(null);
                    if (antiga != null) {
                        antiga.setStatus(StatusMoto.DISPONIVEL);
                        motoRepository.save(antiga);
                    }
                }
            }

            locacao.setMoto(novaMoto);
            novaMoto.setStatus(StatusMoto.EM_USO);
            motoRepository.save(novaMoto);
        }

        if (dto.dataSaida() != null) {
            locacao.setDataSaida(dto.dataSaida());
        }
        if (dto.dataDevolucao() != null) {
            if (dto.dataDevolucao().isBefore(locacao.getDataSaida())) {
                throw new IllegalArgumentException("Data de devoluÃƒÂ§ÃƒÂ£o nÃƒÂ£o pode ser anterior ÃƒÂ  saÃƒÂ­da.");
            }
            locacao.setDataDevolucao(dto.dataDevolucao());

            Moto moto = locacao.getMoto();
            if (moto != null && !repository.existsByMotoIdAndDataDevolucaoIsNull(moto.getId())) {
                moto.setStatus(StatusMoto.DISPONIVEL);
                motoRepository.save(moto);
            }
        }

        if (dto.condicaoEntrega() != null)   locacao.setCondicaoEntrega(dto.condicaoEntrega());
        if (dto.condicaoDevolucao() != null) locacao.setCondicaoDevolucao(dto.condicaoDevolucao());
        if (dto.qrCode() != null)            locacao.setQrCode(dto.qrCode());

        Locacao salvo = repository.save(locacao);
        return toDTO(salvo);
    }

    @Transactional
    @CacheEvict(
            value = { "locacoesFiltradas", "locacoesAtivas", "locacoesPorCliente", "locacoesCount", "motos", "motoById" },
            allEntries = true
    )
    public void delete(Long id) {
        Locacao locacao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LocaÃƒÂ§ÃƒÂ£o nÃƒÂ£o encontrada"));

        Long motoId = locacao.getMoto() != null ? locacao.getMoto().getId() : null;

        repository.deleteById(id);

        if (motoId != null && !repository.existsByMotoIdAndDataDevolucaoIsNull(motoId)) {
            Moto moto = motoRepository.findById(motoId).orElse(null);
            if (moto != null) {
                moto.setStatus(StatusMoto.DISPONIVEL);
                motoRepository.save(moto);
            }
        }
    }

    @Transactional
    @CacheEvict(
            value = { "locacoesFiltradas", "locacoesAtivas", "locacoesPorCliente", "locacoesCount", "motos", "motoById" },
            allEntries = true
    )
    public LocacaoDTO devolver(Long locacaoId, LocalDateTime dataDevolucao, String condicaoDevolucao) {
        Locacao locacao = repository.findById(locacaoId)
                .orElseThrow(() -> new EntityNotFoundException("LocaÃƒÂ§ÃƒÂ£o nÃƒÂ£o encontrada"));

        if (locacao.getDataDevolucao() != null) {
            throw new IllegalStateException("LocaÃƒÂ§ÃƒÂ£o jÃƒÂ¡ estÃƒÂ¡ finalizada.");
        }

        LocalDateTime devolucao = (dataDevolucao != null) ? dataDevolucao : LocalDateTime.now();
        if (devolucao.isBefore(locacao.getDataSaida())) {
            throw new IllegalArgumentException("Data de devoluÃƒÂ§ÃƒÂ£o nÃƒÂ£o pode ser anterior ÃƒÂ  saÃƒÂ­da.");
        }

        locacao.setDataDevolucao(devolucao);
        locacao.setCondicaoDevolucao(condicaoDevolucao);
        Locacao salvo = repository.save(locacao);

        Moto moto = locacao.getMoto();
        if (!repository.existsByMotoIdAndDataDevolucaoIsNull(moto.getId())) {
            moto.setStatus(StatusMoto.DISPONIVEL);
            motoRepository.save(moto);
        }

        return toDTO(salvo);
    }

    @Transactional
    @CacheEvict(
            value = { "locacoesFiltradas", "locacoesAtivas", "locacoesPorCliente", "locacoesCount", "motos", "motoById" },
            allEntries = true
    )
    public int fecharTodasAtivas() {
        var ativas = repository.findByDataDevolucaoIsNull();
        if (ativas.isEmpty()) return 0;

        LocalDateTime agora = LocalDateTime.now();

        ativas.forEach(l -> l.setDataDevolucao(agora));
        repository.saveAllAndFlush(ativas);

        Set<Long> motoIds = ativas.stream()
                .map(l -> l.getMoto() != null ? l.getMoto().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!motoIds.isEmpty()) {
            var motos = motoRepository.findAllById(motoIds);
            motos.forEach(m -> {
                boolean temAtiva = repository.existsByMotoIdAndDataDevolucaoIsNull(m.getId());
                if (!temAtiva) {
                    m.setStatus(StatusMoto.DISPONIVEL);
                    motoRepository.save(m);
                }
            });
        }

        return ativas.size();
    }

    public LocacaoDTO findById(Long id) {
        Locacao l = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LocaÃƒÂ§ÃƒÂ£o nÃƒÂ£o encontrada"));
        return toDTO(l);
    }

    @Cacheable(
            value = "locacoesFiltradas",
            key = "#clienteId + '-' + #motoId + '-' + (#inicio==null?'':#inicio) + '-' + (#fim==null?'':#fim) + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort?:'')"
    )
    public Page<LocacaoDTO> findByFilters(Long clienteId,
                                          Long motoId,
                                          LocalDateTime inicio,
                                          LocalDateTime fim,
                                          Pageable pageable) {
        return repository.findByFilters(clienteId, motoId, inicio, fim, pageable).map(this::toDTO);
    }

    @Cacheable(value = "locacoesAtivas", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort?:'')")
    public Page<LocacaoDTO> findAtivas(Pageable pageable) {
        return repository.findAtivas(pageable).map(this::toDTO);
    }

    @Cacheable(value = "locacoesPorCliente", key = "#clienteId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort?:'')")
    public Page<LocacaoDTO> findByCliente(Long clienteId, Pageable pageable) {
        return repository.findByClienteIdOrderByDataSaidaDesc(clienteId, pageable).map(this::toDTO);
    }

    @Cacheable("locacoesCount")
    public long count() {
        return repository.count();
    }

    private MotoDTO toMotoDTO(Moto m) {
        if (m == null) return null;
        return new MotoDTO(
                m.getId(),
                m.getPlaca(),
                m.getModelo(),
                m.getChassi(),
                m.getStatus() != null ? m.getStatus().name() : null,
                m.getFotoUrl()
        );
    }

    private LocacaoDTO toDTO(Locacao l) {
        return new LocacaoDTO(
                l.getId(),
                l.getDataSaida(),
                l.getDataDevolucao(),
                l.getCondicaoEntrega(),
                l.getCondicaoDevolucao(),
                l.getQrCode(),
                l.getCliente() != null ? l.getCliente().getId() : null,
                l.getMoto() != null ? l.getMoto().getId() : null,
                toMotoDTO(l.getMoto())
        );
    }
}
