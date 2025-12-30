package juste.chambrepro.service.impl;

import juste.chambrepro.dto.requests.ClientRequest;
import juste.chambrepro.dto.responses.ClientResponse;
import juste.chambrepro.entity.Client;
import juste.chambrepro.exception.ConflictException;
import juste.chambrepro.exception.ResourceNotFoundException;
import juste.chambrepro.mapper.ClientMapper;
import juste.chambrepro.repository.ClientRepository;
import juste.chambrepro.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        log.info("Création d'un nouveau client avec email: {}", request.email());

        if (clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("Un client existe déjà avec cet email: " + request.email());
        }

        Client client = clientMapper.toEntity(request);
        client.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        if (request.role() == null || request.role().isEmpty()) {
            client.setRole(juste.chambrepro.enums.Role.USER);
        } else {
            try {
                client.setRole(juste.chambrepro.enums.Role.valueOf(request.role().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Le rôle '" + request.role() + "' n'est pas valide.");
            }
        }

        Client saved = clientRepository.save(client);
        log.info("Client créé avec succès: {}", saved.getTrackingId());

        return clientMapper.toResponse(saved);
    }

    @Override
    public ClientResponse getClientByTrackingId(UUID trackingId) {
        log.debug("Recherche du client: {}", trackingId);

        Client client = clientRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "trackingId", trackingId));

        return clientMapper.toResponse(client);
    }

    @Override
    public List<ClientResponse> getAllClients() {
        log.debug("Récupération de tous les clients");
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteClient(UUID trackingId) {
        log.info("Suppression du client: {}", trackingId);

        Client client = clientRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "trackingId", trackingId));

        clientRepository.delete(client);
        log.info("Client supprimé avec succès: {}", trackingId);
    }
}
