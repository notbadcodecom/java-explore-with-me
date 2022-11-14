package ru.practicum.explorewithme.event;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateTimeStartHourValidator.class)
public @interface DateTimeStartHourValidation {
    String message() default "The start date should be in {value} hours";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int value();
}
