package Services;
import java.sql.SQLException;
import java.util.List;
public interface IService<T> {
    void ajouter(T t) throws SQLException;      // Add
    void modifier(T t) throws SQLException;     // Update
    void supprimer(T t) throws SQLException;    // Delete
    List<T> rechercher() throws SQLException;
}
