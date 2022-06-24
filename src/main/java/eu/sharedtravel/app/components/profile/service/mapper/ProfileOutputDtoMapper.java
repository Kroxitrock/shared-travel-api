package eu.sharedtravel.app.components.profile.service.mapper;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profile.service.dto.ProfileOutputDto;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsOutputDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileSettingsOutputDtoMapper.class, ProfileSettingsOutputDtoMapper.class})
public interface ProfileOutputDtoMapper {

    @Mapping(target = "email", source = "user.email")
    ProfileOutputDto profileToProfileOutputDto(Profile entity);

    ProfileOutputDto profileDtoToProfileOutputDto(ProfileDto profileDto);
}
