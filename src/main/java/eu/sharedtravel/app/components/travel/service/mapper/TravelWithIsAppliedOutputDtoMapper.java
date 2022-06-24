package eu.sharedtravel.app.components.travel.service.mapper;

import eu.sharedtravel.app.components.profile.service.mapper.ProfileOutputDtoMapper;
import eu.sharedtravel.app.components.travel.model.Travel;
import eu.sharedtravel.app.components.travel.service.dto.TravelWithIsAppliedOutputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileOutputDtoMapper.class})
public interface TravelWithIsAppliedOutputDtoMapper {

    @Mapping(target = "from", source = "entity.from.code")
    @Mapping(target = "to", source = "entity.to.code")
    TravelWithIsAppliedOutputDto travelToTravelWithIsAppliedOutputDto(Travel entity);
}
