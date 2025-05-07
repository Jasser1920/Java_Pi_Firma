package Controllers;

import Models.Evenemment;
import Services.EvenemmentService;
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class CalendrierEvenementController implements Initializable {

    @FXML
    private AnchorPane calendarContainer;

    @FXML
    private Button RetourFX;

    private final EvenemmentService evenementService = new EvenemmentService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarView calendarView = new CalendarView();
        Calendar calendar = new Calendar("Événements");

        // Récupérer tous les événements depuis la base de données
        List<Evenemment> evenements = evenementService.afficher();

        int heure = 0; // compteur cyclique

        for (Evenemment ev : evenements) {
            LocalDate date = new java.sql.Date(ev.getDate().getTime()).toLocalDate();

            Entry<String> entry = new Entry<>(ev.getTitre());
            entry.changeStartDate(date);
            entry.changeEndDate(date);

            // Assigner des heures de 8h à 17h en boucle
            int heureAffichage = 8 + (heure % 10);
            entry.changeStartTime(LocalTime.of(heureAffichage, 0));
            entry.changeEndTime(LocalTime.of(heureAffichage, 59));

            calendar.addEntry(entry);
            heure++; // pour passer à l'heure suivante
        }


        CalendarSource source = new CalendarSource("Source");
        source.getCalendars().add(calendar);
        calendarView.getCalendarSources().add(source);

        // Ajout du calendrier à l'AnchorPane
        calendarContainer.getChildren().add(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }

    public void retourEvent() {
        RetourFX.getScene().getWindow().hide();
    }
}
