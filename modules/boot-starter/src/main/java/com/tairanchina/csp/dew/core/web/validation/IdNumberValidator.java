package com.tairanchina.csp.dew.core.web.validation;

import com.ecfront.dew.common.$;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdNumberValidator implements ConstraintValidator<IdNumber, String> {

    @Override
    public void initialize(IdNumber constraintAnnotation) {
        // doNothing
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value) || $.field.validateIdNumber(value);
    }
}
