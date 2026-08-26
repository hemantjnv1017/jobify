package com.jobify.mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HrMailController.class)
@Import(MailExceptionHandler.class)
class HrMailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HrMailService hrMailService;

    @Test
    void sendMail_sendsHardcodedTemplateToHrEmail() throws Exception {
        mockMvc.perform(post("/api/hr/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"hr@example.com\",\"hrName\":\"Priya Sharma\",\"role\":\"Java Backend Developer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"))
                .andExpect(jsonPath("$.to").value("hr@example.com"))
                .andExpect(jsonPath("$.hrName").value("Priya Sharma"))
                .andExpect(jsonPath("$.role").value("Java Backend Developer"))
                .andExpect(jsonPath("$.cc").isArray())
                .andExpect(jsonPath("$.cc").isEmpty());

        verify(hrMailService).sendToHr("hr@example.com", "Priya Sharma", "Java Backend Developer", new java.util.ArrayList<>());
    }

    @Test
    void sendMail_forwardsCcList() throws Exception {
        mockMvc.perform(post("/api/hr/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"hr@example.com","hrName":"Priya Sharma","role":"Java Backend Developer","cc":["cc1@example.com","cc2@example.com"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cc[0]").value("cc1@example.com"))
                .andExpect(jsonPath("$.cc[1]").value("cc2@example.com"));

        verify(hrMailService).sendToHr(
                org.mockito.ArgumentMatchers.eq("hr@example.com"),
                org.mockito.ArgumentMatchers.eq("Priya Sharma"),
                org.mockito.ArgumentMatchers.eq("Java Backend Developer"),
                org.mockito.ArgumentMatchers.argThat(cc ->
                        cc != null && cc.size() == 2
                                && cc.get(0).equals("cc1@example.com")
                                && cc.get(1).equals("cc2@example.com")));
    }

    @Test
    void sendMail_rejectsInvalidCcEmail() throws Exception {
        mockMvc.perform(post("/api/hr/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"hr@example.com","hrName":"Priya Sharma","role":"Java Backend Developer","cc":["not-an-email"]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendMail_rejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/hr/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"hrName\":\"Priya Sharma\",\"role\":\"Java Backend Developer\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendMail_rejectsMissingHrName() throws Exception {
        mockMvc.perform(post("/api/hr/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"hr@example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendMail_rejectsMissingRole() throws Exception {
        mockMvc.perform(post("/api/hr/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"hr@example.com\",\"hrName\":\"Priya Sharma\"}"))
                .andExpect(status().isBadRequest());
    }
}
