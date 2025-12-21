package juste.chambrepro.service;

import juste.chambrepro.dto.requests.ClientRequest;
import juste.chambrepro.dto.responses.ClientResponse;
import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientResponse createClient(ClientRequest request);
    ClientResponse getClientByTrackingId(UUID trackingId);
    List<ClientResponse> getAllClients();
    void deleteClient(UUID trackingId);
}