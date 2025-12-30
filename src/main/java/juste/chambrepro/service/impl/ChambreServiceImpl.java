package juste.chambrepro.service.impl;

import juste.chambrepro.dto.requests.ChambreRequest;
import juste.chambrepro.dto.responses.ChambreResponse;
import juste.chambrepro.entity.Chambre;
import juste.chambrepro.enums.TypeChambre;
import juste.chambrepro.exception.ConflictException;
import juste.chambrepro.exception.ResourceNotFoundException;
import juste.chambrepro.mapper.ChambreMapper;
import juste.chambrepro.repository.ChambreRepository;
import juste.chambrepro.service.ChambreService;
import juste.chambrepro.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChambreServiceImpl implements ChambreService {

    private final ChambreRepository chambreRepository;
    private final ChambreMapper chambreMapper;
    private final MinioService minioService;

    @Override
    @Transactional
    public ChambreResponse createChambre(ChambreRequest request) {
        log.info("Création d'une nouvelle chambre: {}", request.numero());

        if (chambreRepository.existsByNumero(request.numero())) {
            throw new ConflictException("Une chambre existe déjà avec ce numéro: " + request.numero());
        }

        Chambre chambre = chambreMapper.toEntity(request);
        Chambre saved = chambreRepository.save(chambre);

        log.info("Chambre créée avec succès: {}", saved.getTrackingId());
        return chambreMapper.toResponse(saved);
    }

    @Override
    public ChambreResponse getChambreByTrackingId(UUID trackingId) {
        log.debug("Recherche de la chambre: {}", trackingId);

        Chambre chambre = chambreRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Chambre", "trackingId", trackingId));

        return chambreMapper.toResponse(chambre);
    }

    @Override
    public List<ChambreResponse> getAllChambres() {
        log.debug("Récupération de toutes les chambres");
        return chambreRepository.findAll().stream()
                .map(chambreMapper::toResponse)
                .toList();
    }

    @Override
    public List<ChambreResponse> getChambresByType(TypeChambre type) {
        log.debug("Recherche des chambres de type: {}", type);
        return chambreRepository.findByType(type).stream()
                .map(chambreMapper::toResponse)
                .toList();
    }

    @Override
    public List<ChambreResponse> getChambresDisponibles(LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Recherche des chambres disponibles du {} au {}", dateDebut, dateFin);
        return chambreRepository.findChambresDisponibles(dateDebut, dateFin).stream()
                .map(chambreMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChambreResponse updateChambre(UUID trackingId, ChambreRequest request) {
        log.info("Mise à jour de la chambre: {}", trackingId);

        Chambre chambre = chambreRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Chambre", "trackingId", trackingId));

        if (!chambre.getNumero().equals(request.numero()) &&
                chambreRepository.existsByNumero(request.numero())) {
            throw new ConflictException("Une chambre existe déjà avec ce numéro: " + request.numero());
        }

        chambreMapper.updateEntity(chambre, request);
        Chambre updated = chambreRepository.save(chambre);

        log.info("Chambre mise à jour avec succès: {}", trackingId);
        return chambreMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ChambreResponse uploadPhotos(UUID trackingId, MultipartFile[] files) {
        log.info("Upload de photos pour la chambre: {}", trackingId);

        Chambre chambre = chambreRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Chambre", "trackingId", trackingId));

        // Supprimer les anciennes photos
        if (chambre.getPhotoUrls() != null && !chambre.getPhotoUrls().isEmpty()) {
            chambre.getPhotoUrls().forEach(minioService::deleteFile);
            chambre.getPhotoUrls().clear();
        }

        // Uploader les nouvelles photos
        List<String> photoUrls = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String photoUrl = minioService.uploadFile(file, "chambres/");
                photoUrls.add(photoUrl);
            }
        }
        chambre.setPhotoUrls(photoUrls);

        Chambre updated = chambreRepository.save(chambre);
        log.info("Photos uploadées avec succès pour la chambre: {}", trackingId);

        return chambreMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteChambre(UUID trackingId) {
        log.info("Suppression de la chambre: {}", trackingId);

        Chambre chambre = chambreRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Chambre", "trackingId", trackingId));

        if (chambre.getPhotoUrls() != null && !chambre.getPhotoUrls().isEmpty()) {
            chambre.getPhotoUrls().forEach(minioService::deleteFile);
        }

        chambreRepository.delete(chambre);
        log.info("Chambre supprimée avec succès: {}", trackingId);
    }
}
