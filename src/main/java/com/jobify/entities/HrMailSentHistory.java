package com.jobify.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "hr_mail_sent_history")
public class HrMailSentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hr_user_mapping_id", nullable = false)
    private HrUserMapping hrUserMapping;

    @Column(nullable = false, length = 255)
    private String role;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(name = "cc_emails")
    private String ccEmails;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    public Long getId() {
        return id;
    }

    public HrUserMapping getHrUserMapping() {
        return hrUserMapping;
    }

    public void setHrUserMapping(HrUserMapping hrUserMapping) {
        this.hrUserMapping = hrUserMapping;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(String ccEmails) {
        this.ccEmails = ccEmails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
