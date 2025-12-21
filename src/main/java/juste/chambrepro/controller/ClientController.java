package juste.chambrepro.controller;

import jakarta.validation.Valid;
import juste.chambrepro.dto.requests.ClientRequest;
import juste.chambrepro.dto.responses.ClientResponse;
import juste.chambrepro.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse createClient(@Valid @RequestBody ClientRequest request) {
        return clientService.createClient(request);
    }

    @GetMapping("/{trackingId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#trackingId)")
    public ClientResponse getClient(@PathVariable UUID trackingId) {
        return clientService.getClientByTrackingId(trackingId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClientResponse> getAllClients() {
        return clientService.getAllClients();
    }

    @DeleteMapping("/{trackingId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#trackingId)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable UUID trackingId) {
        clientService.deleteClient(trackingId);
    }
}
