package eu.sharedtravel.app.components.profilesettings.service;

import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.repository.ProfileSettingsRepository;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsPatchInputDto;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsPatchInputDtoMapper;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileSettingsService {

    private final ProfileSettingsRepository profileSettingsRepository;

    private final ProfileSettingsPatchInputDtoMapper profileSettingsPatchInputDtoMapper;

    public ProfileSettings getProfileSettings(Long id) {
        return profileSettingsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Profile Settings not found"));
    }

    @Transactional
    public ProfileSettings updateProfileSettingsPatch(Long id, ProfileSettingsPatchInputDto dto) {
        ProfileSettings profileSettings = getProfileSettings(id);

        profileSettingsPatchInputDtoMapper.updateProfileSettingsFromProfileSettingsPatchInputDto(dto, profileSettings);

        return profileSettingsRepository.save(profileSettings);
    }
}
