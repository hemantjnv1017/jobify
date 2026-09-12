package com.jobify.mail;

import com.jobify.user.UserCredentials;
import com.jobify.user.UserMailCredentialsService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HrMailService {

    private static final Logger log = LoggerFactory.getLogger(HrMailService.class);

    static final String SUBJECT_TEMPLATE =
            "Application for %s | Java & Spring Boot | FinTech & Payments | Immediate Joiner";

    static final String CV_RESOURCE = "cv/Hemant_Kumar_CV.pdf";

    static final String CV_FILENAME = "Hemant_Kumar_CV.pdf";

    static final String BODY_TEMPLATE = """
            <html>
            <body style="margin:0; padding:0; font-family:Arial,Helvetica,sans-serif; \
                         font-size:14px; line-height:1.6; color:#333333;">

                <p>Dear %s,</p>

                <p>I hope you’re doing well.</p>

                <p>
                    I’m <strong>Hemant Kumar</strong>, a Software Developer with
                    <strong>2.5+ years of experience</strong> in building backend systems
                    for the <strong>FinTech & Payments</strong> domain.
                </p>

                <p style="margin-bottom:8px;">
                    <strong>Here’s a quick snapshot of my experience:</strong>
                </p>

                <ul style="margin-top:0; padding-left:20px;">
                    <li>
                        <strong>Backend:</strong>
                        Java, Spring Boot, Microservices, REST APIs
                    </li>

                    <li>
                        <strong>Payments:</strong>
                        Payment workflows, UPI/mandates, lending flows & third-party integrations
                    </li>

                    <li>
                        <strong>Data & Messaging:</strong>
                        PostgreSQL, Redis, Kafka & SQL, MongoDB
                    </li>

                    <li>
                        <strong>Engineering:</strong>
                        AWS, GCP, Git, Maven, CI/CD, monitoring & performance optimization
                    </li>

                    <li>
                        <strong>Availability:</strong>
                        <strong style="color:#0b7a3e;">Immediate Joiner</strong>
                    </li>
                </ul>

                <p>
                    I’m currently exploring
                    <strong>%s</strong> opportunities
                    where I can contribute to building scalable, reliable and
                    high-performance backend systems.
                </p>

                <p>
                    I’ve attached my resume for your consideration. If my profile
                    matches any current or upcoming requirement, I’d be glad to
                    connect and discuss the opportunity.
                </p>

                <p>
                    Thank you for your time and consideration.
                </p>

                <p style="margin-bottom:0;">
                    Best regards,<br>
                    <strong>Hemant Kumar</strong><br>
                    +91 7088375575<br>
                </p>

            </body>
            </html>
            """;

    private final UserMailCredentialsService userMailCredentialsService;
    private final SmtpMailSenderFactory smtpMailSenderFactory;

    public HrMailService(
            UserMailCredentialsService userMailCredentialsService,
            SmtpMailSenderFactory smtpMailSenderFactory
    ) {
        this.userMailCredentialsService = userMailCredentialsService;
        this.smtpMailSenderFactory = smtpMailSenderFactory;
    }

    static String subjectFor(String role) {
        return SUBJECT_TEMPLATE.formatted(role);
    }

    static String bodyFor(String hrName, String role) {
        return BODY_TEMPLATE.formatted(hrName, role);
    }

    public void sendToHr(
            String fromEmail,
            String hrEmail,
            String hrName,
            String role,
            ArrayList<String> cc
    ) {
        UserCredentials credentials = userMailCredentialsService.getCredentialsByUserEmail(fromEmail);
        JavaMailSender mailSender = smtpMailSenderFactory.create(
                credentials.getSmtpUsername(),
                credentials.getSmtpPassword()
        );
        String fromAddress = credentials.getSmtpUsername();

        log.info("Sending HR mail from {} to {} ({}) role={} cc={}",
                fromAddress, hrEmail, hrName, role, cc);
        ClassPathResource cv = new ClassPathResource(CV_RESOURCE);
        if (!cv.exists()) {
            log.error("CV PDF missing on classpath: {}", CV_RESOURCE);
            throw new IllegalStateException("CV PDF not found: " + CV_RESOURCE);
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress);
            helper.setTo(hrEmail);
            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.toArray(String[]::new));
            }
            helper.setSubject(subjectFor(role));
            helper.setText(bodyFor(hrName, role), true);
            helper.addAttachment(CV_FILENAME, cv);
            mailSender.send(message);
            log.info("HR mail with CV sent from {} to {}", fromAddress, hrEmail);
        } catch (MessagingException ex) {
            log.error("Failed to prepare HR mail to {}", hrEmail, ex);
            throw new MailPreparationException("Failed to prepare HR mail with CV attachment", ex);
        } catch (MailException ex) {
            log.error("Failed to send HR mail to {}", hrEmail, ex);
            throw ex;
        }
    }
}
