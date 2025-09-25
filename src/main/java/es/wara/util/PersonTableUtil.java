package es.wara.util;

import es.wara.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class PersonTableUtil {

    public static ObservableList<Person> getPersonList() {
        Person john  = new Person("John",  "Lennon",   LocalDate.of(1940, 10, 9));
        Person paul  = new Person("Paul",  "McCartney",LocalDate.of(1942, 6, 18));
        Person george= new Person("George","Harrison", LocalDate.of(1943, 2, 25));
        Person ringo = new Person("Ringo", "Starr",    LocalDate.of(1940, 7, 7));
        return FXCollections.observableArrayList(john, paul, george, ringo);
    }

    // Columnas de ejemplo
    public static TableColumn<Person, String> getFirstNameColumn() {
        TableColumn<Person, String> col = new TableColumn<>("First Name");
        col.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        return col;
    }

    public static TableColumn<Person, String> getLastNameColumn() {
        TableColumn<Person, String> col = new TableColumn<>("Last Name");
        col.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        return col;
    }

    public static TableColumn<Person, java.time.LocalDate> getBirthDateColumn() {
        TableColumn<Person, java.time.LocalDate> col = new TableColumn<>("Birth Date");
        col.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        return col;
    }
}
