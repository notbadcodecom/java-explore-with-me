package ru.practicum.explorewithme.event;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class DateTimeStartHourValidator implements ConstraintValidator<DateTimeStartHourValidation, LocalDateTime> {
    private LocalDateTime startDateTime;

    @Override
    public void initialize(DateTimeStartHourValidation annotation) {
        this.startDateTime = LocalDateTime.now().plusHours(annotation.value());
    }

    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext cxt) {
        return Optional.ofNullable(dateTime).map(d -> d.isAfter(startDateTime)).orElse(true);
    }
}
