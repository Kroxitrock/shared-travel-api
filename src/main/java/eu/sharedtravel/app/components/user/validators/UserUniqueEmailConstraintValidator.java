package eu.sharedtravel.app.components.user.validators;

import eu.sharedtravel.app.components.user.service.UserService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

/**
 * Class that validates whether a user with the supplied email already exists.
 */
@RequiredArgsConstructor
public class UserUniqueEmailConstraintValidator implements ConstraintValidator<UserUniqueEmailConstraint, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return !userService.userExists(email);
    }

}
