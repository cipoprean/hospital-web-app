package com.ciprian.hospital_appointments.util;

import com.ciprian.hospital_appointments.repository.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class CodeGenerator {

    private static final String CODE_SEED = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 10;

    private final PasswordResetRepository passwordResetRepository;

    public String generateCode() {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String code = generateRandomCode();

            if (!passwordResetRepository.findByCode(code).isPresent()) {
                return code;
            }

            log.warn("Code collision detected for {}, Retrying attempt {}/{}", code, i, MAX_ATTEMPTS);
        }

        throw new IllegalStateException("Code collision detected for " + MAX_ATTEMPTS + " attempts. Consider increasing the code length.");
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CODE_SEED.length());
            sb.append(CODE_SEED.charAt(index));
        }

        return sb.toString();
    }
}
