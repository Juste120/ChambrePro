package juste.chambrepro.controller;

import jakarta.validation.Valid;
import juste.chambrepro.dto.requests.ChambreRequest;
import juste.chambrepro.dto.responses.ChambreResponse;
import juste.chambrepro.enums.TypeChambre;
import juste.chambrepro.service.ChambreService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chambres")
@RequiredArgsConstructor
public class ChambreController {

    private final ChambreService chambreService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ChambreResponse createChambre(@Valid @RequestBody ChambreRequest request) {
        return chambreService.createChambre(request);
    }

    @GetMapping("/{trackingId}")
    public ChambreResponse getChambre(@PathVariable UUID trackingId) {
        return chambreService.getChambreByTrackingId(trackingId);
    }

    @GetMapping
    public List<ChambreResponse> getAllChambres() {
        return chambreService.getAllChambres();
    }

    @GetMapping("/type/{type}")
    public List<ChambreResponse> getChambresByType(@PathVariable TypeChambre type) {
        return chambreService.getChambresByType(type);
    }

    @GetMapping("/disponibles")
    public List<ChambreResponse> getChambresDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        if (!dateDebut.isBefore(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin.");
        }
        return chambreService.getChambresDisponibles(dateDebut, dateFin);
    }

    @PutMapping("/{trackingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ChambreResponse updateChambre(
            @PathVariable UUID trackingId,
            @Valid @RequestBody ChambreRequest request) {
        return chambreService.updateChambre(trackingId, request);
    }

    @PostMapping(value = "/{trackingId}/photos", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload photos for a chambre")
    public ChambreResponse uploadPhotos(
            @PathVariable UUID trackingId,
            @RequestPart("files") MultipartFile[] files) {
        return chambreService.uploadPhotos(trackingId, files);
    }

    @DeleteMapping("/{trackingId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChambre(@PathVariable UUID trackingId) {
        chambreService.deleteChambre(trackingId);
    }
}
