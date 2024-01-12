package group.idealworld.dew.core.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The interface Phone.
 *
 * @author gudaoxuri
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { PhoneValidator.class })
public @interface Phone {

    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "手机号错误";

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
         * Value phone [ ].
         *
         * @return the phone [ ]
         */
        Phone[] value();
    }
}
