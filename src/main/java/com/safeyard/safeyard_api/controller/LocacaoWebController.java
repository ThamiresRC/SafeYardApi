package com.safeyard.safeyard_api.controller;

import com.safeyard.safeyard_api.dto.ClienteDTO;
import com.safeyard.safeyard_api.dto.LocacaoDTO;
import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.service.ClienteService;
import com.safeyard.safeyard_api.service.LocacaoService;
import com.safeyard.safeyard_api.service.MotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;

@Controller
@RequiredArgsConstructor
public class LocacaoWebController {

    private final LocacaoService locacaoService;
    private final ClienteService clienteService;
    private final MotoService motoService;


    @GetMapping("/locacoes")
    public String list(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long motoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            Pageable pageable,
            Model model,
            Principal principal
    ) {
        if (pageable == null || pageable.getPageSize() <= 0 || pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    (pageable == null ? 0 : pageable.getPageNumber()),
                    (pageable == null || pageable.getPageSize() <= 0 ? 10 : pageable.getPageSize()),
                    Sort.by(Sort.Direction.DESC, "dataSaida")
            );
        }

        Page<LocacaoDTO> page = locacaoService.findByFilters(clienteId, motoId, inicio, fim, pageable);

        List<ClienteDTO> clientes = clienteService.listarTodos();
        List<MotoDTO> motos = motoService.findAll(Pageable.unpaged()).getContent();

        Map<Long, ClienteDTO> clientesById = clientes.stream()
                .collect(Collectors.toMap(ClienteDTO::id, Function.identity()));
        Map<Long, MotoDTO> motosById = motos.stream()
                .collect(Collectors.toMap(MotoDTO::id, Function.identity()));

        model.addAttribute("username", username(principal));
        model.addAttribute("page", page);
        model.addAttribute("locacoes", page.getContent());
        model.addAttribute("clientes", clientes);
        model.addAttribute("motos", motos);
        model.addAttribute("clientesById", clientesById);
        model.addAttribute("motosById", motosById);

        model.addAttribute("clienteId", clienteId);
        model.addAttribute("motoId", motoId);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);

        return "locacao/list";
    }


    @GetMapping("/locacoes/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes ra, Principal principal) {
        Optional<LocacaoDTO> opt = safeFindById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "LocaÃƒÂ§ÃƒÂ£o #" + id + " nÃƒÂ£o encontrada.");
            return "redirect:/locacoes";
        }
        LocacaoDTO l = opt.get();

        List<ClienteDTO> clientes = clienteService.listarTodos();
        List<MotoDTO> motos = motoService.findAll(Pageable.unpaged()).getContent();
        Map<Long, ClienteDTO> clientesById = clientes.stream()
                .collect(Collectors.toMap(ClienteDTO::id, Function.identity()));
        Map<Long, MotoDTO> motosById = motos.stream()
                .collect(Collectors.toMap(MotoDTO::id, Function.identity()));

        model.addAttribute("username", username(principal));
        model.addAttribute("locacao", l);
        model.addAttribute("cliente", clientesById.get(l.clienteId()));
        model.addAttribute("moto", motosById.get(l.motoId()));
        model.addAttribute("qrUrl", "/locacoes/" + l.id() + "/qrcode");

        return "locacao/detalhe";
    }


    @GetMapping(value = "/locacoes/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> qrcode(@PathVariable Long id) throws Exception {
        Optional<LocacaoDTO> opt = safeFindById(id);

        byte[] png;
        if (opt.isEmpty()) {
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            png = baos.toByteArray();
        } else {
            LocacaoDTO l = opt.get();
            String payload = (l.qrCode() != null && !l.qrCode().isBlank())
                    ? l.qrCode()
                    : ("LOCACAO:" + l.id());
            png = generateQrPng(payload, 280);
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(10)).cachePublic())
                .body(png);
    }

    private Optional<LocacaoDTO> safeFindById(Long id) {
        try {
            LocacaoDTO dto = locacaoService.findById(id);
            return Optional.ofNullable(dto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private byte[] generateQrPng(String text, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bm;
        try {
            bm = writer.encode(new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                    BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int rgb = bm.get(x, y) ? 0x000000 : 0xFFFFFF;
                image.setRGB(x, y, rgb);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private String username(Principal principal) {
        return (principal != null && principal.getName() != null)
                ? principal.getName()
                : "UsuÃƒÂ¡rio";
    }
}
