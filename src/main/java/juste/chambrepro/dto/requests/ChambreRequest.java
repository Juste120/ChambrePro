package juste.chambrepro.dto.requests;

import jakarta.validation.constraints.*;
import juste.chambrepro.enums.TypeChambre;
import java.math.BigDecimal;

public record ChambreRequest(
        @NotBlank(message = "Le numéro de chambre est obligatoire")
        @Size(max = 10, message = "Le numéro ne doit pas dépasser 10 caractères")
        String numero,

        @NotNull(message = "Le type de chambre est obligatoire")
        TypeChambre type,

        @NotNull(message = "Le prix par nuit est obligatoire")
        @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
        BigDecimal prixParNuit,

        @NotNull(message = "Le nombre de lits est obligatoire")
        @Min(value = 1, message = "Il doit y avoir au moins 1 lit")
        Integer nombreLits,

        @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
        String description
) {}