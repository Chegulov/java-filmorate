package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class CorrectLoginValidator implements ConstraintValidator<CorrectLogin, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !(value.contains(" "));
    }
}
