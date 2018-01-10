package com.ecfront.dew.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by 迹_Jason on 2017/7/19.
 * Validation of the IdNumber.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IdNumberValidator.class})
public @interface IdNumber {
    String message() default "身份证号错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @interface List {
        IdNumber[] value();
    }
}
