package juste.chambrepro.service.impl;

import juste.chambrepro.dto.requests.LoginRequest;
import juste.chambrepro.dto.responses.JwtResponse;
import juste.chambrepro.entity.Client;
import juste.chambrepro.repository.ClientRepository;
import juste.chambrepro.security.JwtTokenProvider;
import juste.chambrepro.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRepository clientRepository;

    @Override
    public JwtResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.motDePasse()
                )
        );

        String token = jwtTokenProvider.generateToken(authentication);

        Client client = clientRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        log.info("Connexion réussie pour: {}", request.email());

        return new JwtResponse(
                token,
                client.getTrackingId(),
                client.getEmail(),
                client.getNom(),
                client.getRole()
        );
    }
}