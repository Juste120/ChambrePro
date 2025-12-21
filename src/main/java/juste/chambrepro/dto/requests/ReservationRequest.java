package juste.chambrepro.dto.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationRequest(
        @NotNull(message = "La date de début est obligatoire")
        @Future(message = "La date de début doit être dans le futur")
        LocalDate dateDebut,

        @NotNull(message = "La date de fin est obligatoire")
        @Future(message = "La date de fin doit être dans le futur")
        LocalDate dateFin,

        @NotNull(message = "Le trackingId du client est obligatoire")
        UUID clientTrackingId,

        @NotNull(message = "Le trackingId de la chambre est obligatoire")
        UUID chambreTrackingId
) {}