package eu.sharedtravel.app.components.profile.controller;

import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profile.service.dto.ProfileOutputDto;
import eu.sharedtravel.app.components.profile.service.dto.ProfilePatchInputDto;
import eu.sharedtravel.app.components.profile.service.mapper.ProfileOutputDtoMapper;
import eu.sharedtravel.app.components.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Controller", description = "Profile Endpoints")
public class ProfileController {

    private final ProfileService profileService;

    private final ProfileOutputDtoMapper profileOutputDtoMapper;

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get details about current profile")
    @GetMapping("/me")
    public ProfileOutputDto getProfileDetails(@ResolveUser ProfileDto profileDto) {
        return profileOutputDtoMapper.profileDtoToProfileOutputDto(profileDto);
    }

    @Operation(summary = "Get details about an existing profile")
    @GetMapping("/{id}")
    public ProfileOutputDto getProfileDetails(@PathVariable Long id) {
        Profile profile = profileService.getProfile(id);

        return profileOutputDtoMapper.profileToProfileOutputDto(profile);
    }

    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Update the profile using patch request", security = @SecurityRequirement(name = "JWT"))
    @PatchMapping
    @SuppressWarnings("squid:S4684") // The user is coming from @ResolveUser and is thus safe
    public ProfileOutputDto patchProfile(@RequestBody @Valid ProfilePatchInputDto profilePatchInputDto,
        @ResolveUser User user) {
        Profile profile = profileService.patchUpdateProfile(user, profilePatchInputDto);

        return profileOutputDtoMapper.profileToProfileOutputDto(profile);
    }
}
