package Utils;

import java.util.Map;

public class TraductionUtils {

    private static final Map<String, String> correspondance = Map.ofEntries(
            //  Légumes
            Map.entry("carrot", "carotte"),
            Map.entry("potato", "pomme de terre"),
            Map.entry("onion", "oignon"),

            // Fruits
            Map.entry("apple", "pomme"),
            Map.entry("banana", "banane"),
            Map.entry("orange", "orange"),

            // Viande / Poisson
            Map.entry("chicken", "poulet"),
            Map.entry("beef", "boeuf"),
            Map.entry("fish", "poisson")
    );

    public static String traduire(String motAnglais) {
        return correspondance.getOrDefault(motAnglais.toLowerCase(), motAnglais);
    }
}
