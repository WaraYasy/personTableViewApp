package es.wara.control;

import es.wara.model.Person;
import es.wara.util.PersonTableUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class TableViewController {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDeleteRows;

    @FXML
    private Button btnRestoreRows;

    @FXML
    private DatePicker dateBirth;

    @FXML
    private TableView<Person> tablePerson;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TableColumn<Person, Integer> colPersonId;

    @FXML
    private TableColumn<Person, String> colFirstName;

    @FXML
    private TableColumn<Person, String> colLastName;

    @FXML
    private TableColumn<Person, LocalDate> colBirthDate;

    /** Logger para registrar eventos y depuración de la aplicación */
    private static final Logger loger = LoggerFactory.getLogger(TableViewController.class);

    @FXML
    public void initialize() {
        // Setear la lista inicial
        tablePerson.setItems(PersonTableUtil.getPersonList());

        // Configurar selección múltiple
        TableView.TableViewSelectionModel<Person> tsm = tablePerson.getSelectionModel();
        tsm.setSelectionMode(SelectionMode.MULTIPLE);

        // Agregar columnasa
        colPersonId.setCellValueFactory(new PropertyValueFactory<>("personId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
    }

    @FXML
    public void addPerson() {
        Person p = new Person(txtFirstName.getText(), txtLastName.getText(), dateBirth.getValue());
        tablePerson.getItems().add(p);
        clearFields();
    }

    @FXML
    public void deleteSelectedRows() {
        TableView.TableViewSelectionModel<Person> tsm = tablePerson.getSelectionModel();
        if (tsm.isEmpty()) {
            System.out.println("Please select a row to delete.");
            return;
        }

        ObservableList<Integer> list = tsm.getSelectedIndices();
        Integer[] selectedIndices = list.toArray(new Integer[0]);
        Arrays.sort(selectedIndices);

        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            tsm.clearSelection(selectedIndices[i]);
            tablePerson.getItems().remove((int) selectedIndices[i]);
        }
    }

    @FXML
    public void restoreRows() {
        tablePerson.getItems().clear();
        tablePerson.getItems().addAll(PersonTableUtil.getPersonList());
    }

    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        dateBirth.setValue(null);
    }


}
