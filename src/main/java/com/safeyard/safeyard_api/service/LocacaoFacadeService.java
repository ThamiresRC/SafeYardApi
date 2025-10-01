package com.safeyard.safeyard_api.service;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.LocacaoFormDTO;
import com.safeyard.safeyard_api.dto.LocacaoViewDTO;
import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.model.Cliente;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.repository.ClienteRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocacaoFacadeService {

    private final LocacaoService locacaoService;
    private final ClienteRepository clienteRepository;
    private final MotoRepository motoRepository;

    // --- helpers ---
    private static String digits(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }
    private static String normPlaca(String p) {
        return p == null ? null : p.trim().toUpperCase();
    }

    // --- CREATE ---
    @Transactional
    public LocacaoViewDTO createFromForm(LocacaoFormDTO form) {
        final String cpfSemMascara = digits(form.cpf());
        final String placa = normPlaca(form.placa());

        if (cpfSemMascara == null || cpfSemMascara.isBlank())
            throw new IllegalArgumentException("CPF é obrigatório.");
        if (placa == null || placa.isBlank())
            throw new IllegalArgumentException("Placa é obrigatória.");

        Cliente cliente = clienteRepository.findByCpf(cpfSemMascara)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado para CPF " + form.cpf()));

        Moto moto = motoRepository.findByPlacaIgnoreCase(placa)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada para placa " + placa));

        LocacaoDTO dto = new LocacaoDTO(
                null,
                form.dataRetirada(),
                form.dataDevolucao(),
                "",
                "",
                null,
                cliente.getId(),
                moto.getId(),
                new MotoDTO(
                        moto.getId(),
                        moto.getPlaca(),
                        moto.getModelo(),
                        moto.getChassi(),
                        moto.getStatus() != null ? moto.getStatus().name() : null,
                        moto.getFotoUrl()
                )
        );

        LocacaoDTO saved = locacaoService.create(dto);

        return new LocacaoViewDTO(
                saved.id(),
                cliente.getCpf(),
                cliente.getNome(),
                moto.getPlaca(),
                saved.dataSaida(),
                saved.dataDevolucao()
        );
    }

    // --- UPDATE ---
    @Transactional
    public LocacaoViewDTO updateFromForm(Long id, LocacaoFormDTO form) {
        final String cpfSemMascara = digits(form.cpf());
        final String placa = normPlaca(form.placa());

        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        if (cpfSemMascara == null || cpfSemMascara.isBlank())
            throw new IllegalArgumentException("CPF é obrigatório.");
        if (placa == null || placa.isBlank())
            throw new IllegalArgumentException("Placa é obrigatória.");

        Cliente cliente = clienteRepository.findByCpf(cpfSemMascara)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado para CPF " + form.cpf()));

        Moto moto = motoRepository.findByPlacaIgnoreCase(placa)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada para placa " + placa));

        LocacaoDTO dto = new LocacaoDTO(
                id,
                form.dataRetirada(),
                form.dataDevolucao(),
                "",
                "",
                null,
                cliente.getId(),
                moto.getId(),
                new MotoDTO(
                        moto.getId(),
                        moto.getPlaca(),
                        moto.getModelo(),
                        moto.getChassi(),
                        moto.getStatus() != null ? moto.getStatus().name() : null,
                        moto.getFotoUrl()
                )
        );

        // ajuste o método se no seu service o nome/assinatura for diferente
        LocacaoDTO updated = locacaoService.update(id, dto);

        return new LocacaoViewDTO(
                updated.id(),
                cliente.getCpf(),
                cliente.getNome(),
                moto.getPlaca(),
                updated.dataSaida(),
                updated.dataDevolucao()
        );
    }

    // --- DELETE ---
    @Transactional
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        locacaoService.delete(id);
    }

    // --- LISTAGEM PARA O APP ---
    @Transactional(readOnly = true)
    public List<LocacaoViewDTO> listForForm() {
        // Evita Pageable.unpaged() (alguns repositórios usam pageable nas queries)
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "dataSaida"));

        Page<LocacaoDTO> page = locacaoService.findByFilters(
                null, null, null, null, pageable
        );

        return page.getContent().stream().map(l -> {
            var cliente = (l.clienteId() != null)
                    ? clienteRepository.findById(l.clienteId()).orElse(null)
                    : null;
            var moto = (l.motoId() != null)
                    ? motoRepository.findById(l.motoId()).orElse(null)
                    : null;

            return new LocacaoViewDTO(
                    l.id(),
                    cliente != null ? cliente.getCpf()  : null,
                    cliente != null ? cliente.getNome() : null,
                    moto    != null ? moto.getPlaca()   : null,
                    l.dataSaida(),
                    l.dataDevolucao()
            );
        }).toList();
    }
}
