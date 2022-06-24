package eu.sharedtravel.app.components.profile.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import eu.sharedtravel.app.common.Constants;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.repository.ProfileRepository;
import eu.sharedtravel.app.components.profile.repository.predicate.ProfilePredicates;
import eu.sharedtravel.app.components.profile.service.dto.ProfilePatchInputDto;
import eu.sharedtravel.app.components.profile.service.mapper.ProfilePatchInputDtoMapper;
import eu.sharedtravel.app.components.profile.service.mapper.ProfilePatchInputDtoMapperImpl;
import eu.sharedtravel.app.components.user.model.User;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ProfileService.class, ProfilePatchInputDtoMapperImpl.class})
class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfilePatchInputDtoMapper profilePatchInputDtoMapper;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private ProfilePredicates profilePredicates;

    private Profile profile;

    private BooleanExpression mockExpression;

    @BeforeEach
    public void setUp() {
        profile = ProfileTestMocks.mockUserProfile();
        mockExpression = Expressions.asBoolean(true);
    }

    @Test
    void givenIdShouldReturnProfileWithSameId() {
        Mockito.when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));

        Profile fetchedProfile = profileService.getProfile(profile.getId());
        Assertions.assertEquals(fetchedProfile.getId(), profile.getId());
    }

    @Test
    void givenWrongIdShouldThrowEntityNotFoundException() {
        Long id = Constants.INVALID_ID;

        Mockito.when(profileRepository.findById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> profileService.getProfile(id));
    }

    @Test
    void givenUserShouldReturnProfileWithSameUser() {

        Mockito.when(profilePredicates.forUser(Mockito.any())).thenReturn(mockExpression);
        Mockito.when(profileRepository.findOne(mockExpression)).thenReturn(Optional.of(profile));

        Profile fetchedProfile = profileService.getProfile(profile.getUser());
        Assertions.assertEquals(fetchedProfile.getId(), profile.getId());
    }

    @Test
    void givenWrongUserShouldThrowEntityNotFoundException() {
        User user = new User();

        Mockito.when(profilePredicates.forUser(Mockito.any())).thenReturn(mockExpression);
        Mockito.when(profileRepository.findOne(mockExpression)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> profileService.getProfile(user));
    }

    @Test
    void givenExamplePatchUpdateUserShouldReturnUpdated() {
        Profile updatedProfile = ProfileTestMocks.mockUserProfile();
        updatedProfile.setFirstName(updatedProfile.getFirstName() + "2");

        String initialFirstName = profile.getFirstName();

        ProfilePatchInputDto dto = new ProfilePatchInputDto();
        dto.setFirstName(updatedProfile.getFirstName());

        Mockito.when(profilePredicates.forUser(Mockito.any())).thenReturn(mockExpression);
        Mockito.when(profileRepository.findOne(mockExpression)).thenReturn(Optional.of(profile));
        Mockito.when(profileRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Profile result = profileService.patchUpdateProfile(profile.getUser(), dto);

        Assertions.assertEquals(profile.getId(), result.getId());
        Assertions.assertEquals(updatedProfile.getId(), result.getId());
        Assertions.assertEquals(updatedProfile.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertNotEquals(initialFirstName, result.getFirstName());
    }

    @Test
    void givenUserFirstAndLastNameShouldCreateProfile() {

        profileService.createProfileForUser(profile.getUser(), profile.getFirstName(), profile.getLastName());

        Mockito.verify(profileRepository)
            .save(Mockito.argThat(profileToSave -> profileToSave != null
                && profile.getUser().equals(profileToSave.getUser())
                && profile.getFirstName().equals(profileToSave.getFirstName())
                && profile.getFirstName().equals(profileToSave.getLastName())));
    }
}
