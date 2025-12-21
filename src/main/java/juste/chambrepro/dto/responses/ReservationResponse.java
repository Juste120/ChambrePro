package juste.chambrepro.dto.responses;

import juste.chambrepro.enums.StatutReservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID trackingId,
        LocalDate dateDebut,
        LocalDate dateFin,
        StatutReservation statut,
        UUID clientTrackingId,
        String clientNom,
        UUID chambreTrackingId,
        String chambreNumero,
        LocalDateTime createdAt
) {}