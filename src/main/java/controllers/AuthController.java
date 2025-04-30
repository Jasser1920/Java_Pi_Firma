package controllers;

import Models.Utilisateur;
import Services.UtilisateurService;
import Services.EmailService;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthController {
    private static AuthController instance;
    private Utilisateur currentUser;
    private UtilisateurService utilisateurService;
    private EmailService emailService;
    private Map<String, String> resetCodes;
    private Map<String, Long> codeTimestamps;

    private AuthController() {
        utilisateurService = new UtilisateurService();
        emailService = new EmailService();
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
            Utilisateur user = utilisateurService.findByEmail(email);
            if (user == null) {
                System.out.println("Login failed: No user found for email: " + email);
                return false;
            }
            System.out.println("User found: " + user.getEmail() + ", hashed password: " + user.getMotdepasse());
            if (BCrypt.checkpw(password, user.getMotdepasse()) && user.isVerified()) {
                currentUser = user;
                System.out.println("User logged in successfully: " + user.getEmail());
                return true;
            } else {
                System.out.println("Login failed for email: " + email + " - Password mismatch or user not verified");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQLException during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean signup(Utilisateur user) {
        try {
            String code = emailService.generateAndSendCode(user.getEmail());
            if (code == null) {
                return false;
            }
            String hashedPassword = BCrypt.hashpw(user.getMotdepasse(), BCrypt.gensalt());
            user.setMotdepasse(hashedPassword);
            user.setConfirmationCode(code);
            utilisateurService.ajouter(user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmCode(String code, String email) {
        try {
            Utilisateur user = utilisateurService.findByEmail(email);
            if (user != null && user.getConfirmationCode() != null && user.getConfirmationCode().equals(code)) {
                user.setVerified(true);
                user.setConfirmationCode(null);
                utilisateurService.modifier(user);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendResetCode(String email) {
        try {
            Utilisateur user = utilisateurService.findByEmail(email);
            if (user == null) {
                return false;
            }
            String code = emailService.generateAndSendCode(email);
            if (code == null) {
                return false;
            }
            resetCodes.put(email, code);
            codeTimestamps.put(email, System.currentTimeMillis());
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
        if (System.currentTimeMillis() - timestamp > 600000) {
            resetCodes.remove(email);
            codeTimestamps.remove(email);
            return false;
        }
        if (storedCode.equals(code)) {
            resetCodes.remove(email);
            codeTimestamps.remove(email);
            return true;
        }
        return false;
    }

    public boolean updatePassword(String email, String newPassword) {
        try {
            Utilisateur user = utilisateurService.findByEmail(email);
            if (user == null) {
                return false;
            }
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            user.setMotdepasse(hashedPassword);
            utilisateurService.modifier(user);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utilisateur getCurrentUser() {
        System.out.println("Current user retrieved: " + (currentUser != null ? currentUser.getEmail() : "null"));
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        System.out.println("User logged out");
    }
}