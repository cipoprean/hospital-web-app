package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.domain.Notification;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.NotificationType;
import com.ciprian.hospital_appointments.dto.NotificationDto;
import com.ciprian.hospital_appointments.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender sender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendEmail(NotificationDto notificationDto, User user) {

        try {
            MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(notificationDto.getRecipient());
            helper.setSubject(notificationDto.getSubject());

            if (Objects.nonNull(notificationDto.getTemplateName())) {
                Context context = new Context();
                context.setVariables(notificationDto.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDto.getTemplateName(), context);
                helper.setText(htmlContent, true);
            } else {
                helper.setText(notificationDto.getMessage(), true);
            }

            sender.send(message);
            log.info("Succsessfully sent email to {}", notificationDto.getRecipient());

            var notification = Notification
                    .builder()
                    .recipient(notificationDto.getRecipient())
                    .subject(notificationDto.getSubject())
                    .message(notificationDto.getMessage())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepository.save(notification);

        } catch (Exception e) {
            log.error("Error sending email - {}", e.getMessage());
        }
    }

}
