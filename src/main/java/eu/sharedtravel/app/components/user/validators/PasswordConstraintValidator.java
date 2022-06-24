package eu.sharedtravel.app.components.user.validators;

import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

/**
 * Class that validates passwords against common password rules.
 */
public class PasswordConstraintValidator implements ConstraintValidator<PasswordConstraint, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
            // At least one upper case character required
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            // At least one lower case character required
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            // At least one digit required
            new CharacterRule(EnglishCharacterData.Digit, 1),
            // At least one special character required
            new CharacterRule(EnglishCharacterData.Special, 1),
            // No whitespaces allowed
            new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));

        return result.isValid();
    }
}
