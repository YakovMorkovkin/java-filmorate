package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoBlankInsideValidator implements ConstraintValidator<NoBlankInside, String> {

    @Override
    public void initialize(NoBlankInside constraintAnnotation) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        boolean result = true;
        if(login != null) {
            result = !login.contains(" ");
        }
        return result;
    }
}