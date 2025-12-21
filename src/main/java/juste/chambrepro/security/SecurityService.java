package juste.chambrepro.security;

import juste.chambrepro.entity.Client;
import juste.chambrepro.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final ClientRepository clientRepository;

    public boolean isCurrentUser(UUID trackingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUserEmail = authentication.getName();
        Client targetClient = clientRepository.findByTrackingId(trackingId)
                .orElse(null);

        if (targetClient == null) {
            return false;
        }

        return targetClient.getEmail().equals(currentUserEmail);
    }
}