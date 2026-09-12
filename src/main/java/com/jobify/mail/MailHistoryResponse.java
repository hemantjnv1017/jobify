package com.jobify.mail;

import com.jobify.entities.HrMailSentHistory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record MailHistoryResponse(
        Long id,
        String hrName,
        String hrEmail,
        String role,
        String subject,
        List<String> cc,
        String status,
        String errorMessage,
        LocalDateTime sentTime,
        LocalDateTime createdTime
) {
    static MailHistoryResponse from(HrMailSentHistory history) {
        var mapping = history.getHrUserMapping();
        var hr = mapping.getHr();
        return new MailHistoryResponse(
                history.getId(),
                hr.getName(),
                hr.getEmail(),
                history.getRole(),
                history.getSubject(),
                parseCc(history.getCcEmails()),
                history.getStatus(),
                history.getErrorMessage(),
                history.getSentTime(),
                history.getCreatedTime()
        );
    }

    private static List<String> parseCc(String ccEmails) {
        if (ccEmails == null || ccEmails.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ccEmails.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
