package group.idealworld.dew.core.web.validation;

import com.ecfront.dew.common.$;
import org.springframework.util.ObjectUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Phone validator.
 *
 * @author gudaoxuri
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public void initialize(Phone constraintAnnotation) {
        // doNothing
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ObjectUtils.isEmpty(value) || $.field.validateMobile(value);
    }

}
