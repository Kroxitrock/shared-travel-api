package eu.sharedtravel.app.components.user.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = PasswordsMatchConstraintValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatchConstraint {

    String password();

    String passwordConfirmation();

    String message() default "Password values don't match!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
