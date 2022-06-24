package eu.sharedtravel.app.components.profilesettings.service.mapper;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsPatchInputDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileSettingsPatchInputDtoMapper {

    void updateProfileSettingsFromProfileSettingsPatchInputDto(ProfileSettingsPatchInputDto dto,
        @MappingTarget ProfileSettings profileSettings);
}
