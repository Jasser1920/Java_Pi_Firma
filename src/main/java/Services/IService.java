package Services;
import java.util.List;
public interface IService<T> {
    void ajouter(T t);      // Add
    void modifier(T t);     // Update
    void supprimer(T t);    // Delete
    List<T> rechercher();
}
