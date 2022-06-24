package eu.sharedtravel.app.components.user.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Class that validates whether the password confirmation matches the password.
 */
public class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatchConstraint, Object> {

    private String password;
    private String passwordConfirmation;

    @Override
    public void initialize(PasswordsMatchConstraint constraintAnnotation) {
        this.password = constraintAnnotation.password();
        this.passwordConfirmation = constraintAnnotation.passwordConfirmation();
    }

    @Override
    public boolean isValid(Object value,
        ConstraintValidatorContext context) {

        Object fieldValue = new BeanWrapperImpl(value)
            .getPropertyValue(password);
        Object fieldMatchValue = new BeanWrapperImpl(value)
            .getPropertyValue(passwordConfirmation);

        if (fieldValue != null) {
            return fieldValue.equals(fieldMatchValue);
        }

        return fieldMatchValue == null;
    }
}
