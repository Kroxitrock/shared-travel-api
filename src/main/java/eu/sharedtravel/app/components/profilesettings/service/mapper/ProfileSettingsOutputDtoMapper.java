package eu.sharedtravel.app.components.profilesettings.service.mapper;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsDto;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsOutputDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileSettingsOutputDtoMapper {

    ProfileSettingsOutputDto profileSettingsToProfileSettingsOutputDto(ProfileSettings profileSettings);

    ProfileSettingsOutputDto profileSettingsToProfileSettingsOutputDto(ProfileSettingsDto profileSettingsDto);
}
