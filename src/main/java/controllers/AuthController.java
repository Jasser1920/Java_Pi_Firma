package controllers;

import Models.Utilisateur;
import Services.UtilisateurService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthController {
    private static AuthController instance;
    private Utilisateur currentUser;
    private UtilisateurService utilisateurService;
    private Map<String, String> resetCodes; // Map email to reset code
    private Map<String, Long> codeTimestamps; // Map email to code generation timestamp

    private AuthController() {
        utilisateurService = new UtilisateurService();
        resetCodes = new HashMap<>();
        codeTimestamps = new HashMap<>();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    public boolean login(String email, String password) {
        try {
            Utilisateur user = utilisateurService.findByEmail(email); // Updated method name
            if (user != null && user.getMotdepasse().equals(password) && user.isVerified()) {
                currentUser = user;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean signup(Utilisateur user) {
        try {
            utilisateurService.ajouter(user); // Updated method name to match IService
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmCode(String code, String email) {
        // Simulate code verification (in reality, check against sent code)
        return true; // For simplicity, assume code is always valid
    }

    public boolean sendResetCode(String email) {
        try {
            Utilisateur user = utilisateurService.findByEmail(email); // Updated method name
            if (user == null) {
                return false;
            }
            // Generate a random 6-digit code
            String code = String.valueOf(new Random().nextInt(900000) + 100000);
            resetCodes.put(email, code);
            codeTimestamps.put(email, System.currentTimeMillis());
            // Simulate sending the code via email
            System.out.println("Reset code for " + email + ": " + code);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyResetCode(String code, String email) {
        if (!resetCodes.containsKey(email)) {
            return false;
        }
        String storedCode = resetCodes.get(email);
        Long timestamp = codeTimestamps.get(email);
        // Check if code is expired (e.g., 10 minutes = 600,000 ms)
        if (System.currentTimeMillis() - timestamp > 600000) {
            resetCodes.remove(email);
            codeTimestamps.remove(email);
            return false;
        }
        if (storedCode.equals(code)) {
            resetCodes.remove(email); // Clear the code after verification
            codeTimestamps.remove(email);
            return true;
        }
        return false;
    }

    public boolean updatePassword(String email, String newPassword) {
        try {
            Utilisateur user = utilisateurService.findByEmail(email); // Updated method name
            if (user == null) {
                return false;
            }
            user.setMotdepasse(newPassword);
            utilisateurService.modifier(user); // Updated method name to match IService
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}