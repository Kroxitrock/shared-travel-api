package eu.sharedtravel.app.components.profile.service.mapper;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileSettingsDtoMapper.class})
public interface ProfileDtoMapper {

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "authorities", source = "user.authorities")
    @Mapping(target = "userId", source = "user.id")
    ProfileDto profileToProfileDto(Profile entity);
}
