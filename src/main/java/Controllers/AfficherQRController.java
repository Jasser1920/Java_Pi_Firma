package Controllers;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ResourceBundle;
import java.util.Properties;


public class AfficherQRController implements Initializable {

    @FXML
    private ImageView qrCodeImage;

    @FXML
    private Label labelInfo;

    @FXML
    private Button retourFX;

    @FXML
    private Button btnEnvoyerMail;

    private Image fxQrImage;          // QR au format JavaFX
    private String infosEvenement;    // Texte affiché et encodé dans le QR

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Interface QR chargée");
    }

    public void setQRCodeData(Image fxImage, String infos, String titreEvenement) {
        this.fxQrImage = fxImage;
        this.infosEvenement = infos;

        qrCodeImage.setImage(fxImage);

        // Afficher uniquement le titre de l’événement, en vert
        labelInfo.setText("🎉 " + titreEvenement);
        labelInfo.setStyle("-fx-font-size: 20px; -fx-text-fill: #85c20a; -fx-font-weight: bold;");
    }


    @FXML
    public void retour() {
        Stage stage = (Stage) retourFX.getScene().getWindow();
        stage.close();
    }

    public void envoyerMailAvecPJ(String destinataire, String cheminImage) {
        final String expediteur = "bahar.wissem@gmail.com";
        final String motDePasse = "pheb kihn xnmz yjsd"; // ⚠️ Ne jamais mettre un mot de passe normal ici

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(expediteur, motDePasse);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(expediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Votre QR Code de participation");

            // Corps du mail
            MimeBodyPart texte = new MimeBodyPart();
            texte.setText("Bonjour,\n\nVoici votre QR Code pour participer à l’événement.\n\nCordialement,\nL’équipe Firma.");

            // Pièce jointe
            MimeBodyPart qr = new MimeBodyPart();
            qr.attachFile(new File(cheminImage));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texte);
            multipart.addBodyPart(qr);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Mail envoyé à " + destinataire);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l’envoi du mail : " + e.getMessage());
        }
    }

    @FXML
    public void envoyerParMail() {
        try {
            File qrFichier = saveImageToFile(fxQrImage, "qr_temp.png");

            // Adresse de test — tu peux la rendre dynamique
            String destinataire = "wissal.bahar@gmail.com";

            envoyerMailAvecPJ(destinataire, qrFichier.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde ou l'envoi du QR : " + e.getMessage());
        }
    }




    // Convertit Image JavaFX en .png
    public File saveImageToFile(Image fxImage, String filename) throws IOException {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = fxImage.getPixelReader();
        int[] buffer = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, WritablePixelFormat.getIntArgbInstance(), buffer, 0, width);
        bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);

        File outputFile = new File(filename);
        ImageIO.write(bufferedImage, "png", outputFile);
        return outputFile;
    }
}
