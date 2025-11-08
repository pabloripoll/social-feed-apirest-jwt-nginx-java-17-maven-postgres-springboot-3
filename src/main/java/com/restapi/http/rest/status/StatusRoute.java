package com.restapi.http.rest.status;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatusRoute {

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;

    public StatusRoute(JavaMailSender mailSender, RabbitTemplate rabbitTemplate) {
        this.mailSender = mailSender;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/api/v1/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity
                .status(200)
                .body(Map.of("message", "API up and running."));
    }

    /**
     * Send a simple test email. Mail server configuration must be provided via Spring properties.
     * Example: GET /api/v1/status/send-email?to=test@example.com&subject=hello&body=world
     */
    @GetMapping("/api/v1/status/send-email")
    public ResponseEntity<Map<String, String>> sendEmail(
            /* @RequestParam String to,
            @RequestParam(defaultValue = "Test email from API") String subject,
            @RequestParam(defaultValue = "This is a test email body.") String body */) {

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo("test-user@example.com");
            msg.setSubject("Test email from Java 17 Spring Boot API");
            msg.setText("This is a test email body sent from Java 17 Spring Boot API.");
            // From can be omitted; if required set via properties or here:
            // msg.setFrom("no-reply@example.com");

            mailSender.send(msg);

            return ResponseEntity
                    .status(200)
                    .body(Map.of("message", "email sent (captured by Mailhog if configured)"));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }

    /**
     * Send a simple message to RabbitMQ.
     * Example: GET /api/v1/status/send-rabbit?queue=test-queue&message=hello
     * This sends the message to the default direct exchange with the queue name as routing key.
     */
    @GetMapping("/api/v1/status/send-rabbit")
    public ResponseEntity<Map<String, String>> sendRabbit(
            /* @RequestParam String queue,
            @RequestParam(defaultValue = "test-message") String message */) {

        try {
            // Sends to default exchange with routingKey = queue name.
            rabbitTemplate.convertAndSend("task_queue", "test-message");
            return ResponseEntity
                    .status(200)
                    .body(Map.of("message", "sent to rabbit", "queue", "task_queue"));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }
}
