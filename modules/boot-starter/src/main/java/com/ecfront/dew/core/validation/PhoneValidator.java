package com.ecfront.dew.core.validation;

import com.ecfront.dew.common.$;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by 迹_Jason on 2017/7/19.
 * Validation of the mobile phone.
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public void initialize(Phone constraintAnnotation) {
        // doNothing
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value) || $.field.validateMobile(value);
    }

}
