package com.phonebook;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PBController implements Initializable {

//<editor-fold defaultstate="collapsed" desc="FXML Objects">
    @FXML
            TableView table;
    @FXML
            TextField inputLastName;
    @FXML
            TextField inputFirstName;
    @FXML
            TextField inputEmail;
    @FXML
            Button addNewContactButton;
    @FXML
            StackPane menuPane;
    @FXML
            Pane contactPane;
    @FXML
            Pane exportPane;
//</editor-fold>
    
    private final ObservableList<Person> data =
            FXCollections.observableArrayList(
            new Person("Szabó", "Dávid", "szabod@citromail.com"),
            new Person("Kovács", "Éva", "kovacs_eva@freemail.hu"),
            new Person("Juhász", "Gábor", "jgabor1989@gmailmail.com"));
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TableColumn lastNameCol = new TableColumn("LastName");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        editCommitEvent(lastNameCol, "LastName");
        
        TableColumn firstNameCol = new TableColumn("FirstName");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        editCommitEvent(firstNameCol, "FirstName");
        
        TableColumn emailCol = new TableColumn("EmailAddress");
        emailCol.setMinWidth(200);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
        editCommitEvent(emailCol, "Email");
        
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol);
        table.setItems(data);
    }
    
    private void editCommitEvent(TableColumn tableCol, String cellName) {
        tableCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>> () {
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t) {
                    getCurrentColumn(t, cellName);
                }
            });
    }
    
    private void getCurrentColumn(TableColumn.CellEditEvent<Person, String> t, String name) {
        switch (name) {
            case "LastName":
            ((Person) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
                            ).setLastName(t.getNewValue());
            case "FirstName":
            ((Person) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
                            ).setFirstName(t.getNewValue());
            case "Email":
            ((Person) t.getTableView().getItems().get(
                    t.getTablePosition().getRow())
                            ).setEmail(t.getNewValue());
        }
    }

    
}
