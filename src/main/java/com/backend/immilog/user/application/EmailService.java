package com.backend.immilog.user.application;

import com.backend.immilog.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.backend.immilog.global.exception.ErrorCode.EMAIL_SEND_FAILED;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendHtmlEmail(
            String to,
            String subject,
            String htmlBody
    ) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("your-email@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);  // true는 HTML을 의미함
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
            throw new CustomException(EMAIL_SEND_FAILED);
        }

        javaMailSender.send(message);
    }
}
