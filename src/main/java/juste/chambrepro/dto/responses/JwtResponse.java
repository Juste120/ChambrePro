package juste.chambrepro.dto.responses;

import juste.chambrepro.enums.Role;
import java.util.UUID;

public record JwtResponse(
        String token,
        String type,
        UUID trackingId,
        String email,
        String nom,
        Role role
) {
    public JwtResponse(String token, UUID trackingId, String email, String nom, Role role) {
        this(token, "Bearer", trackingId, email, nom, role);
    }
}