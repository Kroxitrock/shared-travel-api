package eu.sharedtravel.app.common.security;

import eu.sharedtravel.app.components.profile.model.Profile;
import eu.sharedtravel.app.components.profile.service.ProfileService;
import eu.sharedtravel.app.components.profile.service.dto.ProfileDto;
import eu.sharedtravel.app.components.profile.service.mapper.ProfileDtoMapper;
import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import eu.sharedtravel.app.components.user.service.mapper.UserDtoMapper;
import javax.activation.UnsupportedDataTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolver that injects data into an endpoint method parameter annotated with {@link ResolveUser}. The injected
 * parameter must be of the following types:
 * <ul>
 *  <li>{@link String} - the email of the user will be injected to the parameter</li>
 *  <li>{@link UserDto} - the whole security user data will be injected to the parameter</li>
 *  <li>{@link Profile} - The profile entity will be injected to the parameter</li>
 *  <li>{@link ProfileDto} - the whole profile data (along with the user data) will be injected to the parameter</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserResolver implements HandlerMethodArgumentResolver {

    private final ProfileService profileService;

    private final UserDtoMapper userDtoMapper;
    private final ProfileDtoMapper profileDtoMapper;

    /**
     * Validates whether the parameter is supported
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(ResolveUser.class)
            || parameter.hasParameterAnnotation(OptionalUser.class))
            && (parameter.getParameterType().equals(String.class)
            || parameter.getParameterType().equals(User.class)
            || parameter.getParameterType().equals(UserDto.class)
            || parameter.getParameterType().equals(Profile.class)
            || parameter.getParameterType().equals(ProfileDto.class));
    }

    /**
     * Generates the object that is going to be injected in the supported parameter
     *
     * @throws PreAuthenticatedCredentialsNotFoundException when there is no principal in the request
     * @throws UnsupportedDataTypeException                 when an unsupported data type is set to the injection
     *                                                      target
     */
    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
        @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
        throws PreAuthenticatedCredentialsNotFoundException, UnsupportedDataTypeException {

        User user = extractUser(webRequest, parameter);

        if (parameter.getParameterType().equals(String.class)) {
            if (user == null) {
                return null;
            }

            return user.getEmail();
        }

        if (parameter.getParameterType().equals(User.class)) {
            return user;
        }

        if (parameter.getParameterType().equals(UserDto.class)) {
            return userDtoMapper.userToUserDto(user);
        }

        Profile profile = profileService.getProfile(user);

        if (parameter.getParameterType().equals(Profile.class)) {
            return profile;
        }

        if (parameter.getParameterType().equals(ProfileDto.class)) {
            return profileDtoMapper.profileToProfileDto(profile);
        }

        throw new UnsupportedDataTypeException(
            String.format("Datatype %s not supported for by the @ResolveUser annotation",
                parameter.getParameterType().getName()));
    }

    /**
     * Extracts the UserDetails of the request
     *
     * @throws PreAuthenticatedCredentialsNotFoundException when there is no principal in the request
     */
    private User extractUser(NativeWebRequest webRequest, MethodParameter parameter)
        throws PreAuthenticatedCredentialsNotFoundException {

        Authentication principal = (Authentication) webRequest.getUserPrincipal();

        if (principal == null) {
            if (parameter.hasParameterAnnotation(OptionalUser.class)) {
                log.trace("Optional user not!");
                return null;
            }

            log.warn("No principal supplied for @ResolveUser parameter {}.", parameter.getParameterName());

            throw new PreAuthenticatedCredentialsNotFoundException("No principal found for request!");
        }

        return (User) principal.getPrincipal();
    }
}
