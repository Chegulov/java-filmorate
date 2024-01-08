package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = CorrectLoginValidator.class)
public @interface CorrectLogin {
    String message() default "Login must not contain spaces";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}

