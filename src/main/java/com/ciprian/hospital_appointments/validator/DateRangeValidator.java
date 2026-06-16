package com.ciprian.hospital_appointments.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Objects;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    String startDateField;
    String endDateField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.startDateField = annotation.startDate();
        this.endDateField = annotation.endDate();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        try {

            Field start = o.getClass().getDeclaredField("startDate");
            Field end = o.getClass().getDeclaredField("endDate");
            start.setAccessible(true);
            end.setAccessible(true);

            var startDate = (LocalDate) start.get(o);
            var endDate = (LocalDate) end.get(o);

            if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
                return true;
            }

            boolean valid = !endDate.isBefore(startDate);

            if (!valid) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(endDateField)
                        .addConstraintViolation();
            }

            return valid;
        } catch (Exception e) {
            return false;
        }
    }
}
