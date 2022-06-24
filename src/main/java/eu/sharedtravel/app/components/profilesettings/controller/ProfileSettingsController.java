package eu.sharedtravel.app.components.profilesettings.controller;

import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.profilesettings.service.ProfileSettingsService;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsOutputDto;
import eu.sharedtravel.app.components.profilesettings.service.dto.ProfileSettingsPatchInputDto;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsOutputDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles/me/settings")
@RequiredArgsConstructor
@Tag(name = "Profile Settings Controller", description = "Profile Settings Endpoints")
public class ProfileSettingsController {

    private final ProfileSettingsService profileSettingsService;

    private final ProfileSettingsOutputDtoMapper profileSettingsOutputDtoMapper;

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get details about an existing profile's settings", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ProfileSettingsOutputDto getProfileSettingsDetails(@ResolveUser ProfileDto profileDto) {
        ProfileSettings profileSettings = profileSettingsService
            .getProfileSettings(profileDto.getProfileSettings().getId());

        return profileSettingsOutputDtoMapper.profileSettingsToProfileSettingsOutputDto(profileSettings);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Update the profile's settings using patch request", security = @SecurityRequirement(name = "JWT"))
    @PatchMapping
    public ProfileSettingsOutputDto patchProfileSettings(@ResolveUser ProfileDto profileDto,
        @RequestBody @Valid ProfileSettingsPatchInputDto profileSettingsPatchInputDto) {
        ProfileSettings profileSettings = profileSettingsService
            .updateProfileSettingsPatch(profileDto.getProfileSettings().getId(), profileSettingsPatchInputDto);

        return profileSettingsOutputDtoMapper.profileSettingsToProfileSettingsOutputDto(profileSettings);
    }
}
