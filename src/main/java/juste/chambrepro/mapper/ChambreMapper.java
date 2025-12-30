package juste.chambrepro.mapper;

import juste.chambrepro.dto.requests.ChambreRequest;
import juste.chambrepro.dto.responses.ChambreResponse;
import juste.chambrepro.entity.Chambre;
import org.mapstruct.*;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = UUID.class)
public interface ChambreMapper {

    ChambreResponse toResponse(Chambre chambre);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackingId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "photoUrls", ignore = true)
    Chambre toEntity(ChambreRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "photoUrls", ignore = true)
    void updateEntity(@MappingTarget Chambre entity, ChambreRequest request);
}