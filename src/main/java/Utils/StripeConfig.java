package Utils;

import com.stripe.Stripe;
import java.io.IOException;
import java.util.Properties;

public class StripeConfig {
    public static void init() {
    // Clé Stripe en dur (remplace "sk_test_XXXXXXXXXXXXXXXXXXXXXXXX" par ta vraie clé !)
    Stripe.apiKey = "sk_test_51RIolTRqyeNGg6AyQbd3axACvx4GH1gtwRy7ItLYqQjAI4j7MUQzj7Be4ywuP3HJW9R83xZCYgjoiHuq6XAwgWDM00B51uDGCU"; // <-- Mets ta vraie clé ici
    System.out.println("[StripeConfig] Clé Stripe définie en dur !");
}
}