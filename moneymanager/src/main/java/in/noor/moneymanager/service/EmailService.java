package in.noor.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    @Value("${brevo.from.name:Money Manager}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    /**
     * Send simple email (plain text)
     */
    public void sendEmail(String to, String subject, String body) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", fromName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("textContent", body);

        sendRequest(payload);
    }

//    for activation email service
public void sendHtmlEmail(String to, String subject, String htmlBody) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("sender", Map.of("name", fromName, "email", fromEmail));
    payload.put("to", List.of(Map.of("email", to)));
    payload.put("subject", subject);
    payload.put("htmlContent", htmlBody);  // ✅ Changed from textContent to htmlContent

    sendRequest(payload);
}

    /**
     * Send email with attachment
     */
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String filename) {
        String base64File = Base64.getEncoder().encodeToString(attachment);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", fromName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("htmlContent", "<p>" + body + "</p>");
        payload.put("attachment", List.of(Map.of(
                "content", base64File,
                "name", filename
        )));

        sendRequest(payload);
    }

    /**
     * Send HTML email (forgot password, templates, etc.)
     */
    public void sendEmailForgotPassword(String to, String subject, String htmlContent) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", fromName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", to)));
        payload.put("subject", subject);
        payload.put("htmlContent", htmlContent);

        sendRequest(payload);
    }

    /**
     * Common request sender
     */
    private void sendRequest(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(BREVO_URL, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send email: " + response.getBody());
        }
    }
}
