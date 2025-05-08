package Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TranslateAPI {

    public static String traduire(String text, String sourceLang, String targetLang) {
        try {
            URL url = new URL("https://libretranslate.de/translate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String params = "q=" + text +
                    "&source=" + sourceLang +
                    "&target=" + targetLang +
                    "&format=text";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();

            // Extraire "translatedText":"..."
            int start = response.indexOf(":\"") + 2;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);

        } catch (Exception e) {
            e.printStackTrace();
            return text; // si échec, on renvoie le mot original
        }
    }
}
