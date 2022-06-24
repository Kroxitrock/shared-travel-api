package eu.sharedtravel.app.components.travel.service.mapper;

import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.dto.TravelUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TravelUpdateDtoMapper {

    @Mapping(target = "from.code", source = "from")
    @Mapping(target = "to.code", source = "to")
    Travel updateTravelFromTravelUpdateDto(TravelUpdateDto dto, @MappingTarget Travel entity);
}
