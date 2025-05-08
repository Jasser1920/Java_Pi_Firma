package Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class CohereTextImprover {
    private static final String API_KEY = "wxZtvH8fr7yDhq03tHaN18LR9astCF32MrIuUoFH"; // ← remplace ici
    private static final String API_URL = "https://api.cohere.ai/v1/generate";

    public static String improveText(String inputText) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        MediaType mediaType = MediaType.parse("application/json");
        String json = """
                {
                    "model": "command-light",
                    "prompt": "Rewrite this product description to make it more attractive and engaging for customers. D'ont writ a too long description max 20 words. And  make it professional ans appelet to clients. All the description is about food:\\n%s\\nNew description:",
                    "max_tokens": 200,
                    "temperature": 0.7
                }
                """.formatted(inputText);

        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonNode jsonNode = mapper.readTree(responseBody);
            return jsonNode.get("generations").get(0).get("text").asText().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur : impossible d'améliorer le texte.";
        }
    }
}
