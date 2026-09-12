package com.jobify.mail;

import com.jobify.entities.Hr;
import com.jobify.entities.HrMailSentHistory;
import com.jobify.entities.HrUserMapping;
import com.jobify.entities.User;
import com.jobify.entities.UserCredentials;
import com.jobify.repository.HrMailSentHistoryRepository;
import com.jobify.repository.HrRepository;
import com.jobify.repository.HrUserMappingRepository;
import com.jobify.repository.UserRepository;
import com.jobify.user.UserCredentialsNotFoundException;
import com.jobify.user.UserNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HrMailService {

    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";

    @Value("${email.email-enabled}")
    private boolean emailEnabled;

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

    private final UserRepository userRepository;
    private final HrRepository hrRepository;
    private final HrUserMappingRepository hrUserMappingRepository;
    private final HrMailSentHistoryRepository hrMailSentHistoryRepository;
    private final SmtpMailSenderFactory smtpMailSenderFactory;

    public HrMailService(
            UserRepository userRepository,
            HrRepository hrRepository,
            HrUserMappingRepository hrUserMappingRepository,
            HrMailSentHistoryRepository hrMailSentHistoryRepository,
            SmtpMailSenderFactory smtpMailSenderFactory
    ) {
        this.userRepository = userRepository;
        this.hrRepository = hrRepository;
        this.hrUserMappingRepository = hrUserMappingRepository;
        this.hrMailSentHistoryRepository = hrMailSentHistoryRepository;
        this.smtpMailSenderFactory = smtpMailSenderFactory;
    }

    static String subjectFor(String role) {
        return SUBJECT_TEMPLATE.formatted(role);
    }

    static String bodyFor(String hrName, String role) {
        return BODY_TEMPLATE.formatted(hrName, role);
    }

    @Transactional
    public void sendToHr(
            String fromEmail,
            String hrEmail,
            String hrName,
            String role,
            ArrayList<String> cc
    ) {
        User user = userRepository.findActiveWithCredentialsByEmail(fromEmail)
                .orElseThrow(() -> new UserNotFoundException(fromEmail));
        UserCredentials credentials = user.getCredentials();
        if (credentials == null) {
            throw new UserCredentialsNotFoundException(fromEmail);
        }

        Hr hr = findOrCreateHr(hrEmail, hrName);
        if (!hr.isActive()) {
            throw new IllegalStateException("HR contact is inactive: " + hrEmail);
        }

        HrUserMapping mapping = findOrCreateMapping(user, hr);
        String subject = subjectFor(role);
        String ccEmails = formatCcEmails(cc);

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
            helper.setTo(hr.getEmail());
            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.toArray(String[]::new));
            }
            helper.setSubject(subject);
            helper.setText(bodyFor(hr.getName(), role), true);
            helper.addAttachment(CV_FILENAME, cv);

            if (emailEnabled) {
                mailSender.send(message);
                saveMailHistory(mapping, role, subject, ccEmails, STATUS_SENT, null, LocalDateTime.now());
                log.info("HR mail with CV sent from {} to {}", fromAddress, hrEmail);
            } else {
                saveMailHistory(mapping, role, subject, ccEmails, STATUS_SENT, null, LocalDateTime.now());
                log.info("Email sending disabled; recorded history without SMTP send for {}", hrEmail);
            }
        } catch (MessagingException ex) {
            log.error("Failed to prepare HR mail to {}", hrEmail, ex);
            saveMailHistory(mapping, role, subject, ccEmails, STATUS_FAILED, ex.getMessage(), null);
            throw new MailPreparationException("Failed to prepare HR mail with CV attachment", ex);
        } catch (MailException ex) {
            log.error("Failed to send HR mail to {}", hrEmail, ex);
            saveMailHistory(mapping, role, subject, ccEmails, STATUS_FAILED, ex.getMessage(), null);
            throw ex;
        }
    }

    private Hr findOrCreateHr(String hrEmail, String hrName) {
        return hrRepository.findByEmail(hrEmail)
                .map(existing -> {
                    if (!existing.getName().equals(hrName)) {
                        existing.setName(hrName);
                        return hrRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Hr hr = new Hr();
                    hr.setName(hrName);
                    hr.setEmail(hrEmail);
                    Hr saved = hrRepository.save(hr);
                    log.info("Created HR contact id={} email={}", saved.getId(), hrEmail);
                    return saved;
                });
    }

    private HrUserMapping findOrCreateMapping(User user, Hr hr) {
        return hrUserMappingRepository.findByUser_IdAndHr_Id(user.getId(), hr.getId())
                .map(existing -> {
                    if (!existing.isActive()) {
                        existing.setActive(true);
                        return hrUserMappingRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    HrUserMapping mapping = new HrUserMapping();
                    mapping.setUser(user);
                    mapping.setHr(hr);
                    HrUserMapping saved = hrUserMappingRepository.save(mapping);
                    log.info("Created HR user mapping id={} userId={} hrId={}",
                            saved.getId(), user.getId(), hr.getId());
                    return saved;
                });
    }

    private void saveMailHistory(
            HrUserMapping mapping,
            String role,
            String subject,
            String ccEmails,
            String status,
            String errorMessage,
            LocalDateTime sentTime
    ) {
        HrMailSentHistory history = new HrMailSentHistory();
        history.setHrUserMapping(mapping);
        history.setRole(role);
        history.setSubject(subject);
        history.setCcEmails(ccEmails);
        history.setStatus(status);
        history.setErrorMessage(errorMessage);
        history.setSentTime(sentTime);
        hrMailSentHistoryRepository.save(history);
        log.info("Saved mail history id={} status={} mappingId={}",
                history.getId(), status, mapping.getId());
    }

    private static String formatCcEmails(ArrayList<String> cc) {
        if (cc == null || cc.isEmpty()) {
            return null;
        }
        return cc.stream().collect(Collectors.joining(","));
    }

    @Transactional(readOnly = true)
    public List<MailHistoryResponse> listMailHistory() {
        return hrMailSentHistoryRepository.findAllOrderByCreatedTimeDesc().stream()
                .map(MailHistoryResponse::from)
                .toList();
    }
}
