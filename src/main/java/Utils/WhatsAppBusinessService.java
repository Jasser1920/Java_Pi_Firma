package Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WhatsAppBusinessService {
    private static final String API_URL = "https://graph.facebook.com/v22.0/603475056190651/messages";
    private static final String ACCESS_TOKEN = "EAAOvYp0egBcBOxHDFKdduXGYnhG9jm6Aly0po7h7pKQE7aPlBYklbwfQXd9zAAmxl0E2CRZCZCsEvjZCZC1cLrNomSCG7couwZBaEM9fk31zAPtxjkrzZCpm2f1lQBImo4ZBaeHVJqa17hZAifk96sHY2tgyvSfE9S8BB1548pjY8Dx5nqUizqBvJ8PunOAGxVAyHHVs8ene0m3tCjyx37f4HXAJcOw98061JrUfnhmJ2BQZD";

    public static void sendReclamationStatus(String toPhoneNumber, String status, String adminResponse) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = String.format(
                    "{ \"messaging_product\": \"whatsapp\", \"to\": \"%s\", \"type\": \"text\", \"text\": { \"body\": \"Votre réclamation a été traitée. Nous vous invitons à consulter la rubrique Réclamations dans votre profil.\\nL’équipe El Firma.\" } }",
                    toPhoneNumber
            );

            // System prompt for debugging
            System.out.println("[SYSTEM PROMPT] WhatsApp message will be sent.");
            System.out.println("[SYSTEM PROMPT] Recipient number: " + toPhoneNumber);
            System.out.println("[SYSTEM PROMPT] Message content (status): " + status);
            System.out.println("[SYSTEM PROMPT] Message content (admin response): " + adminResponse);


            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                System.out.println("WhatsApp message sent successfully to " + toPhoneNumber);
            } else {
                System.out.println("Failed to send WhatsApp message. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
