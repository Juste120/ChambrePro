package juste.chambrepro.controller;

import jakarta.validation.Valid;
import juste.chambrepro.dto.requests.ReservationRequest;
import juste.chambrepro.dto.responses.ReservationResponse;
import juste.chambrepro.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @GetMapping("/{trackingId}")
    @PreAuthorize("isAuthenticated()")
    public ReservationResponse getReservation(@PathVariable UUID trackingId) {
        return reservationService.getReservationByTrackingId(trackingId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/client/{clientTrackingId}/actives")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#clientTrackingId)")
    public List<ReservationResponse> getReservationsActives(@PathVariable UUID clientTrackingId) {
        return reservationService.getReservationsActives(clientTrackingId);
    }

    @PatchMapping("/{trackingId}/annuler")
    @PreAuthorize("isAuthenticated()")
    public ReservationResponse annulerReservation(@PathVariable UUID trackingId) {
        return reservationService.annulerReservation(trackingId);
    }
}