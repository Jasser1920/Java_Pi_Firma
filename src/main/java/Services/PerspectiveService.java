package Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PerspectiveService {
    private static final double TOXICITY_THRESHOLD = 0.3;
    private static final String API_KEY = "AIzaSyDts3DkM124prh94SEnQFUDEMDfhwyLa2E";
    private static final String PERSPECTIVE_API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze";
    private final ObjectMapper objectMapper;

    public PerspectiveService() {
        this.objectMapper = new ObjectMapper();
    }

    public boolean isContentAppropriate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return true;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(PERSPECTIVE_API_URL + "?key=" + API_KEY);

            // Construire le corps de la requête avec plus d'attributs
            Map<String, Object> requestedAttributes = new HashMap<>();
            requestedAttributes.put("TOXICITY", new HashMap<>());
            requestedAttributes.put("SEVERE_TOXICITY", new HashMap<>());
            requestedAttributes.put("IDENTITY_ATTACK", new HashMap<>());
            requestedAttributes.put("INSULT", new HashMap<>());
            requestedAttributes.put("PROFANITY", new HashMap<>());
            requestedAttributes.put("THREAT", new HashMap<>());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("comment", Map.of("text", text));
            requestBody.put("requestedAttributes", requestedAttributes);
            requestBody.put("languages", new String[]{"fr"});

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            request.setEntity(new StringEntity(jsonBody));
            request.setHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("API Response: " + responseBody); // Log de la réponse
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                if (jsonResponse.has("attributeScores")) {
                    JsonNode scores = jsonResponse.get("attributeScores");
                    
                    // Vérifier tous les attributs
                    String[] attributes = {"TOXICITY", "SEVERE_TOXICITY", "IDENTITY_ATTACK", "INSULT", "PROFANITY", "THREAT"};
                    for (String attribute : attributes) {
                        if (scores.has(attribute) && 
                            scores.get(attribute).has("summaryScore")) {
                            
                            double score = scores.get(attribute)
                                    .get("summaryScore")
                                    .get("value")
                                    .asDouble();
                            
                            System.out.println(attribute + " score: " + score); // Log des scores
                            
                            if (score > TOXICITY_THRESHOLD) {
                                System.out.println("Content rejected due to high " + attribute + " score");
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error analyzing content: " + e.getMessage());
            return true;
        }
    }
} 