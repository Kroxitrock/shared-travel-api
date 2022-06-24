package eu.sharedtravel.app.components.user.validators;

import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

/**
 * Class that validates whether an email is in the right format.
 */
@RequiredArgsConstructor
public class UserEmailFormatConstraintValidator implements ConstraintValidator<UserEmailFormatConstraint, String> {

    private static final String EMAIL_REGEX = "^(.+)@(\\S+)\\.(\\S+)$";


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isValidEmail(value);
    }

    private boolean isValidEmail(String email) {
        return Pattern
            .compile(EMAIL_REGEX)
            .matcher(email)
            .matches();
    }
}
