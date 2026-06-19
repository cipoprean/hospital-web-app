package com.ciprian.hospital_appointments.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@UtilityClass
@Slf4j
public class NameUtil {

    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_KEY = "lastName";

    public static Map<String, String> parseFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Numele nu poate fi gol");
        }

        String trimmedFullName = fullName.trim();
        int firstSpace = trimmedFullName.indexOf(' ');

        if (firstSpace == -1) {
            throw new IllegalArgumentException("Numele trebuie să conțină nume și prenume");
        }

        String lastName = trimmedFullName.substring(0, firstSpace).trim();
        String firstName = trimmedFullName.substring(firstSpace + 1).trim();

        if (lastName.isBlank() || firstName.isBlank()) {
            throw new IllegalArgumentException("Nume sau prenume invalid");
        }

        return Map.of(FIRST_NAME_KEY, firstName, LAST_NAME_KEY, lastName);
    }
}
