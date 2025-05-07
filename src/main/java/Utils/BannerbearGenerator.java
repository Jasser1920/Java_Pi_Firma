/*package Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BannerbearGenerator {

    public static void generatePoster(String jsonPayload) {
        try {
            URL url = new URL("https://api.bannerbear.com/v2/images");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + BannerbearConfig.API_KEY);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("🟡 Code de réponse : " + code);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/