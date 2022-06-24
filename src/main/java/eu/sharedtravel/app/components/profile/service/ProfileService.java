package eu.sharedtravel.app.components.profile.service;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.repository.ProfileRepository;
import eu.sharedtravel.app.components.profile.repository.predicate.ProfilePredicates;
import eu.sharedtravel.app.components.profile.service.dto.ProfilePatchInputDto;
import eu.sharedtravel.app.components.profile.service.mapper.ProfilePatchInputDtoMapper;
import eu.sharedtravel.app.components.profilesettings.model.ProfileSettings;
import eu.sharedtravel.app.components.user.model.User;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfilePredicates profilePredicates;

    private final ProfilePatchInputDtoMapper profilePatchInputDtoMapper;

    public Profile getProfile(Long id) {
        return profileRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Profile with id %d not found", id)));
    }

    public Profile getProfile(User user) {
        return profileRepository.findOne(profilePredicates.forUser(user)).orElseThrow(
            () -> new EntityNotFoundException(String.format("Profile with userId %d not found", user.getId())));
    }

    @Transactional
    public Profile patchUpdateProfile(User user, ProfilePatchInputDto dto) {
        Profile profile = getProfile(user);

        profilePatchInputDtoMapper.updateProfileFromProfilePatchInputDto(dto, profile);

        return profileRepository.save(profile);
    }

    @Transactional
    public void createProfileForUser(User user, String firstName, String lastName) {
        log.trace("Creating profile for user {}...", user);
        Profile profile = Profile.builder()
            .user(user)
            .firstName(firstName)
            .lastName(lastName)
            .profileSettings(new ProfileSettings())
            .build();

        profileRepository.save(profile);

        log.trace("Profile {} created!", profile);
    }
}
