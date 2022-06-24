package eu.sharedtravel.app.components.travel.service.mapper;

import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.dto.TravelInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TravelInputDtoMapper {

    @Mapping(target = "from.code", source = "from")
    @Mapping(target = "to.code", source = "to")
    Travel travelInputDtoToTravel(TravelInputDto dto);
}
