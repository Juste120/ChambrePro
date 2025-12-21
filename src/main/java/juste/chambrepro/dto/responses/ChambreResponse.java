package juste.chambrepro.dto.responses;

import juste.chambrepro.enums.TypeChambre;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChambreResponse(
        UUID trackingId,
        String numero,
        TypeChambre type,
        BigDecimal prixParNuit,
        Integer nombreLits,
        String description,
        String photoUrl,
        LocalDateTime createdAt
) {}
