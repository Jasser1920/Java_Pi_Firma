package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class ScraperGeant {
    public static void main(String[] args) {
        try {
            // Désactiver la vérification SSL pour accéder au site
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Connexion au site
            Document doc = Jsoup.connect("https://www.geantdrive.tn/tunis-city/160-le-marche?q=Cat%C3%A9gories-%C5%92ufs-L%C3%A9gumes-Fruits-Boucherie+et+volaille-Poissonnerie-Charcuterie-Fruits+secs%2C+s%C3%A9ch%C3%A9s+et+confits").get();



            Elements produits = doc.select(".thumbnail-container");


            for (Element produit : produits) {
                String nom = produit.select(".product-title").text();
                String prix = produit.select(".price").text();
                System.out.println(nom + " → " + prix);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
