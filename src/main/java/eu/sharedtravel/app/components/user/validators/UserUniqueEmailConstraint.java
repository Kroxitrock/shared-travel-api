package eu.sharedtravel.app.components.user.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = UserUniqueEmailConstraintValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserUniqueEmailConstraint {

    String message() default "Email already taken!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
