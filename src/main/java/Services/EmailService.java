package Services;

import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    public String generateAndSendCode(String recipientEmail) {
        String code = String.format("%06d", new Random().nextInt(999999));
        boolean sent = sendEmail(recipientEmail, code);
        if (!sent) {
            System.err.println("Failed to send email to: " + recipientEmail);
            return null;
        }
        return code;
    }

    private boolean sendEmail(String to, String code) {
        final String username = "firmaprojectpi@gmail.com";
        final String password = "acuarzqgkiufkkhv";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Your Verification Code - " + System.currentTimeMillis());
            message.setText("Your verification code is: " + code);
            Transport.send(message);
            System.out.println("Email sent to: " + to);
            return true;
        } catch (MessagingException e) {
            System.err.println("Email sending failed: " + e.getMessage());
            return false;
        }
    }
}