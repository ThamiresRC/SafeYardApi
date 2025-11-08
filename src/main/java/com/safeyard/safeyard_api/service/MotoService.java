package com.safeyard.safeyard_api.service;

import com.safeyard.safeyard_api.dto.MotoDTO;
import com.safeyard.safeyard_api.model.Moto;
import com.safeyard.safeyard_api.model.Moto.StatusMoto;
import com.safeyard.safeyard_api.repository.LocacaoRepository;
import com.safeyard.safeyard_api.repository.MotoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MotoService {

    private final MotoRepository repository;
    private final LocacaoRepository locacaoRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadRoot;

    private static final long MAX_BYTES = 3 * 1024 * 1024; // 3MB
    private static final Set<String> ALLOWED_MIME = Set.of("image/jpeg", "image/jpg", "image/png");

    @CacheEvict(value = {"motos"}, allEntries = true)
    public MotoDTO create(MotoDTO dto) {
        String placa  = normalizePlaca(dto.placa());
        String chassi = normalizeChassi(dto.chassi());

        if (repository.existsByPlacaIgnoreCaseAndIdNot(placa, -1L)) {
            throw new IllegalArgumentException("JÃƒÂ¡ existe outra moto com a placa " + placa);
        }
        if (repository.existsByChassiIgnoreCaseAndIdNot(chassi, -1L)) {
            throw new IllegalArgumentException("JÃƒÂ¡ existe outra moto com o chassi " + chassi);
        }

        Moto moto = Moto.builder()
                .placa(placa)
                .modelo(dto.modelo())
                .chassi(chassi)
                .status(parseStatus(dto.status()))
                .build();

        return toDTO(repository.save(moto));
    }

    @CacheEvict(value = {"motoById", "motos"}, allEntries = true)
    public MotoDTO update(Long id, MotoDTO dto) {
        Moto moto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto nÃƒÂ£o encontrada"));

        String novaPlaca  = dto.placa()  != null ? normalizePlaca(dto.placa())   : null;
        String novoChassi = dto.chassi() != null ? normalizeChassi(dto.chassi()) : null;

        if (novaPlaca != null && !novaPlaca.equalsIgnoreCase(moto.getPlaca())) {
            if (repository.existsByPlacaIgnoreCaseAndIdNot(novaPlaca, id)) {
                throw new IllegalArgumentException("JÃƒÂ¡ existe outra moto com a placa " + novaPlaca);
            }
            moto.setPlaca(novaPlaca);
        }

        if (novoChassi != null && !novoChassi.equalsIgnoreCase(moto.getChassi())) {
            if (repository.existsByChassiIgnoreCaseAndIdNot(novoChassi, id)) {
                throw new IllegalArgumentException("JÃƒÂ¡ existe outra moto com o chassi " + novoChassi);
            }
            moto.setChassi(novoChassi);
        }

        moto.setModelo(dto.modelo());

        if (dto.status() != null && !dto.status().isBlank()) {
            moto.setStatus(parseStatus(dto.status()));
        }

        return toDTO(repository.save(moto));
    }

    @CacheEvict(value = {"motoById", "motos"}, allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Cacheable("motos")
    public Page<MotoDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Cacheable(value = "motoById", key = "#id")
    public MotoDTO findById(Long id) {
        Moto moto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto nÃƒÂ£o encontrada"));
        return toDTO(moto);
    }

    public List<MotoDTO> findDisponiveis() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "modelo"))
                .stream()
                .filter(m -> m.getStatus() == StatusMoto.DISPONIVEL)
                .filter(m -> !locacaoRepository.existsByMotoIdAndDataDevolucaoIsNull(m.getId()))
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    @CacheEvict(value = {"motoById", "motos"}, allEntries = true)
    public String salvarImagem(Long id, MultipartFile file) {
        Moto moto = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto nÃƒÂ£o encontrada"));

        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Arquivo nÃƒÂ£o enviado.");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("Arquivo maior que 3MB.");

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            try { contentType = Files.probeContentType(Paths.get(Objects.requireNonNull(file.getOriginalFilename(), "arquivo"))); }
            catch (IOException ignored) {}
        }
        if ("image/jpg".equalsIgnoreCase(contentType)) contentType = "image/jpeg";
        if (contentType == null || !ALLOWED_MIME.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Tipo de arquivo nÃƒÂ£o permitido. Use JPG ou PNG.");
        }

        Path root = Paths.get(uploadRoot, "motos");
        try { Files.createDirectories(root); }
        catch (IOException e) { throw new RuntimeException("Falha ao preparar diretÃƒÂ³rio de upload.", e); }

        String original = file.getOriginalFilename();
        String safeOriginal = (original == null || original.isBlank()) ? "sem_nome" : original.replaceAll("[^a-zA-Z0-9._-]", "_");

        String ext = extensionFrom(contentType, safeOriginal);
        String filename = "moto_" + id + "_" + Instant.now().toEpochMilli() + ext;

        Path dest = root.resolve(filename).normalize().toAbsolutePath();
        if (!dest.startsWith(root.toAbsolutePath())) throw new SecurityException("Caminho de arquivo invÃƒÂ¡lido.");

        if (moto.getFotoUrl() != null && moto.getFotoUrl().startsWith("/files/")) {
            String rel = moto.getFotoUrl().substring("/files/".length());
            Path antigo = Paths.get(uploadRoot).resolve(rel).normalize().toAbsolutePath();
            if (antigo.startsWith(Paths.get(uploadRoot).toAbsolutePath())) {
                try { Files.deleteIfExists(antigo); } catch (IOException ignore) {}
            }
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem.", e);
        }

        String publicUrl = "/files/motos/" + filename;
        moto.setFotoUrl(publicUrl);
        repository.save(moto);
        return publicUrl;
    }

    @Cacheable("motosCount")
    public long count() {
        return repository.count();
    }

    private MotoDTO toDTO(Moto moto) {
        return new MotoDTO(
                moto.getId(),
                moto.getPlaca(),
                moto.getModelo(),
                moto.getChassi(),
                moto.getStatus() == null ? null : moto.getStatus().name(),
                moto.getFotoUrl()
        );
    }

    private String normalizePlaca(String placa) {
        return placa == null ? null : placa.trim().toUpperCase();
    }

    private String normalizeChassi(String chassi) {
        return chassi == null ? null : chassi.trim().toUpperCase();
    }

    private StatusMoto parseStatus(String raw) {
        if (raw == null || raw.isBlank()) return StatusMoto.DISPONIVEL;
        String norm = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try { return StatusMoto.valueOf(norm); } catch (IllegalArgumentException e) { return StatusMoto.DISPONIVEL; }
    }

    private String extensionFrom(String contentType, String fallbackName) {
        if ("image/png".equalsIgnoreCase(contentType)) return ".png";
        if ("image/jpeg".equalsIgnoreCase(contentType) || "image/jpg".equalsIgnoreCase(contentType)) return ".jpg";
        String ext = getExtensionSafe(fallbackName);
        return (".png".equals(ext) || ".jpg".equals(ext) || ".jpeg".equals(ext)) ? ext : ".jpg";
    }

    private String getExtensionSafe(String filename) {
        if (filename == null) return ".jpg";
        int i = filename.lastIndexOf('.');
        if (i < 0 || i == filename.length() - 1) return ".jpg";
        String ext = filename.substring(i).toLowerCase();
        return ext.length() <= 10 ? ext : ".jpg";
    }
}
