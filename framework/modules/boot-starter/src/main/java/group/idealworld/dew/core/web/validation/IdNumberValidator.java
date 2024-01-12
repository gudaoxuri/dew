package group.idealworld.dew.core.web.validation;

import com.ecfront.dew.common.$;
import org.springframework.util.ObjectUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Id number validator.
 *
 * @author gudaoxuri
 */
public class IdNumberValidator implements ConstraintValidator<IdNumber, String> {

    @Override
    public void initialize(IdNumber constraintAnnotation) {
        // doNothing
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ObjectUtils.isEmpty(value) || $.field.validateIdNumber(value);
    }
}
