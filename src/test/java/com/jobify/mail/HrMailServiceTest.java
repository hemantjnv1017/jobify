package com.jobify.mail;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrMailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void sendToHr_usesHardcodedTemplateAndAttachesCv() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        HrMailService hrMailService = new HrMailService(mailSender, "hemantjnv1017@gmail.com");

        hrMailService.sendToHr("hr@company.com", "Priya Sharma", "Java Backend Developer", new ArrayList<>());

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();
        assertThat(sent.getFrom()[0].toString()).contains("hemantjnv1017@gmail.com");
        assertThat(sent.getAllRecipients()[0].toString()).isEqualTo("hr@company.com");
        assertThat(sent.getSubject()).isEqualTo(HrMailService.subjectFor("Java Backend Developer"));

        Multipart multipart = (Multipart) sent.getContent();
        assertThat(containsText(multipart, "Dear Priya Sharma,")).isTrue();
        assertThat(containsText(multipart, "<strong>Java Backend Developer</strong>")).isTrue();
        assertThat(sent.getRecipients(Message.RecipientType.CC)).isNull();
        assertThat(hasAttachment(multipart, HrMailService.CV_FILENAME)).isTrue();
    }

    @Test
    void sendToHr_setsCcRecipients() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        HrMailService hrMailService = new HrMailService(mailSender, "hemantjnv1017@gmail.com");

        hrMailService.sendToHr(
                "hr@company.com",
                "Priya Sharma",
                "Java Backend Developer",
                new ArrayList<>(List.of("cc1@example.com", "cc2@example.com"))
        );

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getRecipients(Message.RecipientType.CC))
                .extracting(address -> ((InternetAddress) address).getAddress())
                .containsExactly("cc1@example.com", "cc2@example.com");
    }

    private static boolean containsText(Multipart multipart, String expected) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            Object content = multipart.getBodyPart(i).getContent();
            if (content instanceof String text && text.contains(expected)) {
                return true;
            }
            if (content instanceof Multipart nested && containsText(nested, expected)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAttachment(Multipart multipart, String filename) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (filename.equals(part.getFileName())) {
                return true;
            }
            Object content = part.getContent();
            if (content instanceof Multipart nested && hasAttachment(nested, filename)) {
                return true;
            }
        }
        return false;
    }
}
