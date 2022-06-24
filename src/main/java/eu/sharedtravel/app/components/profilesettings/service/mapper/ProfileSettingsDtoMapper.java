package eu.sharedtravel.app.components.profilesettings.service.mapper;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileSettingsDtoMapper {

    ProfileSettingsDto profileSettingsToProfileSettingsDto(ProfileSettings profileSettings);
}
