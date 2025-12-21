package juste.chambrepro.service;

import juste.chambrepro.dto.requests.ChambreRequest;
import juste.chambrepro.dto.responses.ChambreResponse;
import juste.chambrepro.enums.TypeChambre;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ChambreService {
    ChambreResponse createChambre(ChambreRequest request);
    ChambreResponse getChambreByTrackingId(UUID trackingId);
    List<ChambreResponse> getAllChambres();
    List<ChambreResponse> getChambresByType(TypeChambre type);
    List<ChambreResponse> getChambresDisponibles(LocalDate dateDebut, LocalDate dateFin);
    ChambreResponse updateChambre(UUID trackingId, ChambreRequest request);
    ChambreResponse uploadPhoto(UUID trackingId, MultipartFile file);
    void deleteChambre(UUID trackingId);
}