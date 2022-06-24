package eu.sharedtravel.app.components.profile.service.mapper;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfilePatchInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfilePatchInputDtoMapper {

    @Mapping(target = "user.email", source = "email")
    void updateProfileFromProfilePatchInputDto(ProfilePatchInputDto dto, @MappingTarget Profile profile);
}
