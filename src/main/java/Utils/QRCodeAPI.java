package Utils;

import javafx.scene.image.Image;

import java.net.URLEncoder;

public class QRCodeAPI {

    public static Image generateQRCode(String content, int width, int height) {
        try {
            String baseUrl = "https://api.qrserver.com/v1/create-qr-code/";
            String url = baseUrl + "?size=" + width + "x" + height + "&data=" + URLEncoder.encode(content, "UTF-8");
            return new Image(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
