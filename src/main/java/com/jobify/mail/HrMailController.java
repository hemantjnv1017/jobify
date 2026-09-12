package com.jobify.mail;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
public class HrMailController {

    private static final Logger log = LoggerFactory.getLogger(HrMailController.class);

    private final HrMailService hrMailService;

    public HrMailController(HrMailService hrMailService) {
        this.hrMailService = hrMailService;
    }

    @GetMapping("/mail/history")
    public ResponseEntity<List<MailHistoryResponse>> mailHistory() {
        return ResponseEntity.ok(hrMailService.listMailHistory());
    }

    @PostMapping("/mail")
    public ResponseEntity<Map<String, Object>> sendMail(@Valid @RequestBody SendHrMailRequest request) {
        log.info("Received HR mail request for {} ({}) role={} cc={}",
                request.email(), request.hrName(), request.role(), request.cc());

        hrMailService.sendToHr(
//                request.fromEmail(),
                "hemantjnv1017@gmail.com",
                request.email(),
                request.hrName(),
                request.role(),
                request.cc()
        );
        return ResponseEntity.ok(Map.of(
                "status", "sent",
                "to", request.email(),
                "hrName", request.hrName(),
                "role", request.role(),
                "cc", request.cc()
        ));
    }
}
