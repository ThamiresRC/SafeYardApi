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
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Moto moto = motoRepository.findById(dto.motoId())
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        LocalDateTime saida = dto.dataSaida() != null ? dto.dataSaida() : LocalDateTime.now();

        LocalDateTime previsaoDevolucao = dto.dataDevolucao();
        if (previsaoDevolucao != null && previsaoDevolucao.isBefore(saida)) {
            throw new IllegalArgumentException("Data de devolução prevista não pode ser anterior à saída.");
        }

        LocalDateTime fim = (previsaoDevolucao != null) ? previsaoDevolucao : saida.plusYears(100);
        if (repository.existsOverlapForMoto(moto.getId(), saida, fim)) {
            throw new IllegalArgumentException("Já existe locação ativa/colidente para esta moto no período informado.");
        }

        String obsDevolucao = dto.condicaoDevolucao();
        if (previsaoDevolucao != null) {
            String tagPrev = " (Devolução prevista: " + previsaoDevolucao + ")";
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
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada"));

        if (dto.clienteId() != null && !dto.clienteId().equals(
                locacao.getCliente() != null ? locacao.getCliente().getId() : null)) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
            locacao.setCliente(novoCliente);
        }

        Long motoIdAtual = locacao.getMoto() != null ? locacao.getMoto().getId() : null;
        if (dto.motoId() != null && !dto.motoId().equals(motoIdAtual)) {
            Moto novaMoto = motoRepository.findById(dto.motoId())
                    .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

            LocalDateTime saida = dto.dataSaida() != null ? dto.dataSaida() : locacao.getDataSaida();
            LocalDateTime fim = (dto.dataDevolucao() != null ? dto.dataDevolucao()
                    : (locacao.getDataDevolucao() != null ? locacao.getDataDevolucao() : saida.plusYears(100)));

            if (repository.existsOverlapForMotoExcludingId(novaMoto.getId(), id, saida, fim)) {
                throw new IllegalArgumentException("Já existe locação ativa/colidente para esta moto no período informado.");
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
                throw new IllegalArgumentException("Data de devolução não pode ser anterior à saída.");
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
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada"));

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
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrada"));

        if (locacao.getDataDevolucao() != null) {
            throw new IllegalStateException("Locação já está finalizada.");
        }

        LocalDateTime devolucao = (dataDevolucao != null) ? dataDevolucao : LocalDateTime.now();
        if (devolucao.isBefore(locacao.getDataSaida())) {
            throw new IllegalArgumentException("Data de devolução não pode ser anterior à saída.");
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

    @Transactional(readOnly = true)
    public LocacaoDTO findById(Long id) {
        Locacao l = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Locação não encontrado"));
        return toDTO(l);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "locacoesFiltradas",
            key = "#clienteId + '-' + #motoId + '-' + (#inicio==null?'':#inicio) + '-' + (#fim==null?'':#fim) + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort?:'')"
    )
    public Page<LocacaoDTO> findByFilters(Long clienteId,
                                          Long motoId,
                                          LocalDateTime inicio,
                                          LocalDateTime fim,
                                          Pageable pageable) {

        Pageable sane = ensureDefaultAndDedupSort(pageable);
        return repository.findByFilters(clienteId, motoId, inicio, fim, sane).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "locacoesAtivas", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort?:'')")
    public Page<LocacaoDTO> findAtivas(Pageable pageable) {
        Pageable sane = ensureDefaultAndDedupSort(pageable);
        return repository.findAtivas(sane).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "locacoesPorCliente", key = "#clienteId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#pageable.sort?:'')")
    public Page<LocacaoDTO> findByCliente(Long clienteId, Pageable pageable) {
        Pageable sane = ensureDefaultAndDedupSort(pageable);
        return repository.findByClienteIdOrderByDataSaidaDesc(clienteId, sane).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable("locacoesCount")
    public long count() {
        return repository.count();
    }

    private Pageable ensureDefaultAndDedupSort(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 10, Sort.by(Order.desc("dataSaida"), Order.desc("id")));
        }
        Sort sort = pageable.getSort();
        Sort sane;
        if (sort == null || sort.isUnsorted()) {
            sane = Sort.by(Order.desc("dataSaida"), Order.desc("id"));
        } else {
            sane = dedup(sort).and(Sort.by(Order.desc("id")));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sane);
    }

    private Sort dedup(Sort sort) {
        Map<String, Order> unique = new LinkedHashMap<>();
        for (Order o : sort) {
            String key = normalizeProp(o.getProperty());
            unique.putIfAbsent(key, normalizeOrder(o));
        }
        return Sort.by(new ArrayList<>(unique.values()));
    }

    private String normalizeProp(String prop) {
        return prop == null ? "" : prop.replace("_", "").toLowerCase(Locale.ROOT);
    }

    private Order normalizeOrder(Order o) {
        String p = o.getProperty();
        if ("datasaida".equals(normalizeProp(p))) p = "dataSaida";
        if ("datadevolucao".equals(normalizeProp(p))) p = "dataDevolucao";
        return new Order(o.getDirection(), p, o.getNullHandling());
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
