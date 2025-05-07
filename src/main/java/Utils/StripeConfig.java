package Utils;

import com.stripe.Stripe;
import java.io.IOException;
import java.util.Properties;

public class StripeConfig {
    public static void init() {
        Properties properties = new Properties();
        try {
            properties.load(StripeConfig.class.getClassLoader().getResourceAsStream("application.properties"));
            String secretKey = properties.getProperty("stripe.secret.key");

            // Vérifier si la clé est définie
            if (secretKey == null || secretKey.isEmpty()) {
                throw new RuntimeException("Erreur : La clé secrète Stripe n'est pas définie dans application.properties");
            }

            Stripe.apiKey = secretKey; // Configure la clé secrète pour toutes les requêtes Stripe

            // Débogage
            System.out.println("Clé secrète Stripe configurée : " + secretKey);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement des clés Stripe : " + e.getMessage());
        }
    }
}