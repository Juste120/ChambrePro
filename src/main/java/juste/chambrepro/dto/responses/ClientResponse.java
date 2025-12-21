package juste.chambrepro.dto.responses;

import juste.chambrepro.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID trackingId,
        String nom,
        String email,
        Role role,
        LocalDateTime createdAt
) {}