package eu.sharedtravel.app.components.secuirty;

import eu.sharedtravel.app.common.security.OptionalUser;
import eu.sharedtravel.app.common.security.ResolveUser;
import eu.sharedtravel.app.common.security.UserResolver;
import eu.sharedtravel.app.components.profile.ProfileTestMocks;
import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profile.service.mapper.ProfileDtoMapper;
import eu.sharedtravel.app.components.profile.service.mapper.ProfileDtoMapperImpl;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsDtoMapper;
import eu.sharedtravel.app.components.profilesettings.service.mapper.ProfileSettingsDtoMapperImpl;
import eu.sharedtravel.app.components.user.UserTestMocks;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import eu.sharedtravel.app.components.user.service.mapper.UserDtoMapper;
import eu.sharedtravel.app.components.user.service.mapper.UserDtoMapperImpl;
import javax.activation.UnsupportedDataTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserResolver.class, UserDtoMapperImpl.class, ProfileDtoMapperImpl.class,
    ProfileSettingsDtoMapperImpl.class})
class UserResolverTest {

    @Autowired
    private UserResolver userResolver;

    @Autowired
    private UserDtoMapper userDtoMapper;
    @Autowired
    private ProfileDtoMapper profileDtoMapper;
    @Autowired
    private ProfileSettingsDtoMapper profileSettingsDtoMapper;

    @MockBean
    private ProfileService profileService;

    private MethodParameter parameter;


    @BeforeEach
    public void setUp() {
        parameter = Mockito.mock(MethodParameter.class);
    }

    @Test
    void givenOptionalUserWithValidTypeShouldReturnTrue() {

        Mockito.when(parameter.getParameterType()).thenAnswer(invocation -> String.class);
        Mockito.when(parameter.hasParameterAnnotation(OptionalUser.class)).thenReturn(true);

        boolean result = userResolver.supportsParameter(parameter);

        Assertions.assertTrue(result);
    }

    @Test
    void givenAnnotatedStringShouldReturnTrue() {
        givenAnnotatedParameterFromValidTypeReturnTrue(String.class);
    }

    @Test
    void givenAnnotatedUserDtoShouldReturnTrue() {
        givenAnnotatedParameterFromValidTypeReturnTrue(UserDto.class);
    }

    @Test
    void givenAnnotatedProfileShouldReturnTrue() {
        givenAnnotatedParameterFromValidTypeReturnTrue(Profile.class);
    }

    @Test
    void givenAnnotatedProfileDtoDtoShouldReturnTrue() {
        givenAnnotatedParameterFromValidTypeReturnTrue(ProfileDto.class);
    }

    @Test
    void givenAnnotatedParameterFromWrongTypeShouldReturnFalse() {
        boolean result = getResultForIsClassSupported(Object.class);

        Assertions.assertFalse(result);
    }

    @Test
    void givenNotAnnotatedParameterShouldReturnFalse() {
        Mockito.when(parameter.hasParameterAnnotation(ResolveUser.class)).thenReturn(false);

        boolean result = userResolver.supportsParameter(parameter);

        Assertions.assertFalse(result);
    }

    private void givenAnnotatedParameterFromValidTypeReturnTrue(Class<?> clazz) {
        boolean result = getResultForIsClassSupported(clazz);

        Assertions.assertTrue(result);
    }

    private boolean getResultForIsClassSupported(Class<?> clazz) {
        Mockito.when(parameter.getParameterType()).thenAnswer(invocation -> clazz);
        Mockito.when(parameter.hasParameterAnnotation(ResolveUser.class)).thenReturn(true);

        return userResolver.supportsParameter(parameter);
    }

    @Test
    void givenStringParameterTypeShouldExtractUserEmail() throws UnsupportedDataTypeException {
        User user = UserTestMocks.mockNormalUser();

        String result = (String) givenValidParameterTypeGetResult(user, String.class);

        Assertions.assertEquals(user.getEmail(), result);
    }

    @Test
    void givenUserParameterTypeShouldExtractUser() throws UnsupportedDataTypeException {
        User user = UserTestMocks.mockNormalUser();

        User result = (User) givenValidParameterTypeGetResult(user, User.class);

        Assertions.assertEquals(user, result);
    }

    @Test
    void givenUserDtoParameterTypeShouldExtractUserInDtoFormat() throws UnsupportedDataTypeException {
        User user = UserTestMocks.mockNormalUser();
        UserDto userDto = UserTestMocks.mockUserDto();

        UserDto result = (UserDto) givenValidParameterTypeGetResult(user, UserDto.class);

        Assertions.assertEquals(userDto, result);
    }

    @Test
    void givenProfileParameterTypeShouldExtractIt() throws UnsupportedDataTypeException {
        User user = UserTestMocks.mockNormalUser();
        Profile profile = ProfileTestMocks.mockUserProfile();

        Mockito.when(profileService.getProfile(user)).thenReturn(profile);

        Profile result = (Profile) givenValidParameterTypeGetResult(user, Profile.class);

        Assertions.assertEquals(profile, result);
    }

    @Test
    void givenProfileDtoParameterTypeShouldExtractItInDtoFormat() throws UnsupportedDataTypeException {
        User user = UserTestMocks.mockNormalUser();
        Profile profile = ProfileTestMocks.mockUserProfile();
        ProfileDto profileDto = ProfileTestMocks.mockUserProfileDto();

        Mockito.when(profileService.getProfile(user)).thenReturn(profile);

        ProfileDto result = (ProfileDto) givenValidParameterTypeGetResult(user, ProfileDto.class);

        Assertions.assertEquals(profileDto, result);
    }


    @Test
    void givenValidParameterTypeAndNullValueWhenUserOptionalShouldReturnNull() throws UnsupportedDataTypeException {
        Mockito.when(parameter.getParameterType()).thenAnswer(invocation -> String.class);
        Mockito.when(parameter.hasParameterAnnotation(OptionalUser.class)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(null);
        NativeWebRequest webRequest = new ServletWebRequest(request);

        Object result = userResolver.resolveArgument(parameter, Mockito.mock(ModelAndViewContainer.class),
            webRequest, Mockito.mock(WebDataBinderFactory.class));

        Assertions.assertNull(result);

    }

    /**
     * Method that tests the extracting of an object for a specified type
     *
     * @param user  is the mocked authentication principal
     * @param clazz is the type of the object we try to extract
     * @return the extracted object
     */
    private Object givenValidParameterTypeGetResult(User user, Class<?> clazz) throws UnsupportedDataTypeException {
        Authentication principal = Mockito.mock(Authentication.class);
        Mockito.when(principal.getPrincipal()).thenReturn(user);

        Mockito.when(parameter.getParameterType()).thenAnswer(invocation -> clazz);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setUserPrincipal(principal);
        NativeWebRequest webRequest = new ServletWebRequest(request);

        Object result = userResolver.resolveArgument(parameter, Mockito.mock(ModelAndViewContainer.class),
            webRequest, Mockito.mock(WebDataBinderFactory.class));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(clazz, result.getClass());

        return result;
    }

    @Test
    void givenRequestWithNoPrincipalShouldThrowPreAuthenticatedCredentialsNotFoundException() {
        Mockito.when(parameter.getParameterName()).thenReturn("test");

        MockHttpServletRequest request = new MockHttpServletRequest();
        NativeWebRequest webRequest = new ServletWebRequest(request);

        Assertions.assertThrows(PreAuthenticatedCredentialsNotFoundException.class,
            () -> userResolver.resolveArgument(parameter, Mockito.mock(ModelAndViewContainer.class),
                webRequest, Mockito.mock(WebDataBinderFactory.class)));
    }
}
