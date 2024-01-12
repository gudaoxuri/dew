package group.idealworld.dew.core.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The interface Id number.
 *
 * @author gudaoxuri
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { IdNumberValidator.class })
public @interface IdNumber {
    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "身份证号错误";

    /**
     * Groups class [ ].
     *
     * @return the class [ ]
     */
    Class<?>[] groups() default {};

    /**
     * Payload class [ ].
     *
     * @return the class [ ]
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * The interface List.
     */
    @interface List {
        /**
         * Value id number [ ].
         *
         * @return the id number [ ]
         */
        IdNumber[] value();
    }
}
