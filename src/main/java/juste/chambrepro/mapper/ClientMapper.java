package juste.chambrepro.mapper;

import juste.chambrepro.dto.requests.ClientRequest;
import juste.chambrepro.dto.responses.ClientResponse;
import juste.chambrepro.entity.Client;
import org.mapstruct.*;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = UUID.class)
public interface ClientMapper {

    ClientResponse toResponse(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackingId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "motDePasse", ignore = true)
    Client toEntity(ClientRequest request);
}