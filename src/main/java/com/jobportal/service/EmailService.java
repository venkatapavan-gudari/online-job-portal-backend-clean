package com.jobportal.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to JobPortal! 🚀");

            String htmlContent = "<h3>Hello " + name + ",</h3>"
                    + "<p>Welcome to JobPortal! We are thrilled to have you on board.</p>"
                    + "<p>Start exploring opportunities or posting jobs today.</p>"
                    + "<br><p>Best regards,<br>The JobPortal Team</p>";

            helper.setText(htmlContent, true); // true sets it as HTML

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendApplicationConfirmationEmail(String toEmail, String applicantName, String jobTitle,
            String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Application Received: " + jobTitle);

            String htmlContent = "<h3>Hello " + applicantName + ",</h3>"
                    + "<p>Your application for the position of <strong>" + jobTitle + "</strong> at <strong>"
                    + companyName + "</strong> has been successfully received.</p>"
                    + "<p>The recruiter will review your profile and get back to you soon.</p>"
                    + "<br><p>Best regards,<br>The JobPortal Team</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Application confirmation email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send application confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendApplicationStatusUpdateEmail(String toEmail, String applicantName, String jobTitle,
            String companyName, String status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Application Status Update: " + jobTitle);

            String htmlContent = "<h3>Hello " + applicantName + ",</h3>"
                    + "<p>The status of your application for the position of <strong>" + jobTitle
                    + "</strong> at <strong>" + companyName + "</strong> has been updated to: <strong>" + status
                    + "</strong>.</p>"
                    + "<p>Log in to your JobPortal dashboard for more details.</p>"
                    + "<br><p>Best regards,<br>The JobPortal Team</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Application status update email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send application status update email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendNewJobPostedEmail(String toEmail, String userName, String jobTitle, String companyName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("New Job Alert: " + jobTitle + " at " + companyName);

            String htmlContent = "<h3>Hello " + userName + ",</h3>"
                    + "<p>A new job opportunity might interest you:</p>"
                    + "<p><strong>Role:</strong> " + jobTitle + "<br>"
                    + "<strong>Company/Recruiter:</strong> " + companyName + "</p>"
                    + "<p>Log in to JobPortal to view more details and apply.</p>"
                    + "<br><p>Best regards,<br>The JobPortal Team</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("New job email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send new job email to {}: {}", toEmail, e.getMessage());
        }
    }
}
