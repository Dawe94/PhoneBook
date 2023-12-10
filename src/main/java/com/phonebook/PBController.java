package com.phonebook;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PBController implements Initializable {

    private final String MENU_CONTACTS = "Contacts";
    private final String MENU_EXIT = "Exit";
    private final String MENU_LIST = "List";
    private final String MENU_EXPORTS = "Exports";

//<editor-fold defaultstate="collapsed" desc="FXML Objects">
    @FXML
    private TableView table;
    @FXML
    private TextField inputLastName;
    @FXML
    private TextField inputFirstName;
    @FXML
    private TextField inputEmail;
    @FXML
    private Button addNewContactButton;
    @FXML
    private StackPane menuPane;
    @FXML
    private Pane contactPane;
    @FXML
    private Pane exportPane;
    @FXML
    private Button exportButton;
    @FXML
    private TextField inputExport;
//</editor-fold>

    // Adatbázis híján egy mesterséges Lista létrehozása
    private final ObservableList<Person> data
            = FXCollections.observableArrayList(
                    new Person("Szabó", "Dávid", "szabod@citromail.com"),
                    new Person("Kovács", "Éva", "kovacs_eva@freemail.hu"),
                    new Person("Juhász", "Gábor", "jgabor1989@gmailmail.com"));

    @FXML
    public void addContact(ActionEvent event) {
        String email = inputEmail.getText();
        if (isEmailAFormatValid(email)) {
            data.add(new Person(inputLastName.getText(), inputFirstName.getText(), email));
            inputLastName.clear();
            inputFirstName.clear();
            inputEmail.clear();
        }
    }

    @FXML
    public void exportPdf(ActionEvent event) {
        String fileName = inputExport.getText();
        fileName = fileName.replaceAll("\\s+", "");
        if (fileName != null && !fileName.isEmpty()) {
            PdfGeneration pdf = new PdfGeneration();
            pdf.pdfGenerator(fileName, data);
            inputExport.clear();
        }
    }

    private boolean isEmailAFormatValid(String email) {
        String regex = "^[A-Z[a-z][0-9]]+(?:[\\._][A-Z[a-z][0-9]]+)*@[A-Z[a-z][0-9]]*(?:-[A-Z[a-z][0-9]]+)*"
                + "\\.[A-Z[a-z][0-9]]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DB db = new DB();
        setTableData();
        setMenuData();
    }

    //<editor-fold defaultstate="collapsed" desc="setTableData">
    public void setTableData() {
        /* A nézet oszlopainak létrehozása
        * minimális méretük beállítása
        * A Person osztályal való összefűzése
        * A felülírást megvalósító metódus hívása */
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

        // Az oszlopok a táblára való felhelyezése
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol);
        table.setItems(data);
    }

    private void editCommitEvent(TableColumn tableCol, String cellName) {
        // Esemény kezelő megvalósítása
        tableCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> t) {
                // Oszlop kiválasztó metódus hívása
                getCurrentColumn(t, cellName);
            }
        });
    }

    private void getCurrentColumn(TableColumn.CellEditEvent<Person, String> t, String name) {
        // A megfelelő oszlop adott cellájának felülírása
        switch (name) {
            case "LastName":
                ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setLastName(t.getNewValue());
                break;
            case "FirstName":
                ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setFirstName(t.getNewValue());
                break;
            case "Email":
                ((Person) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())).setEmail(t.getNewValue());
                break;
        }
    }
//</editor-fold>

    private void setMenuData() {
        // Fő menü létrehozása
        TreeItem<String> treeItemRoot = new TreeItem<>("Menu");
        TreeView<String> treeView = new TreeView<>(treeItemRoot);

        //A fő menü láthatóságának kikapcsolása
        treeView.setShowRoot(false);

        // A főmenü alatti menük létrehozása (ezek már láthatóak)
        TreeItem<String> treeItemContacts = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> treeItemExit = new TreeItem<>(MENU_EXIT);

        // Contacts menü alapértelmezett lenyitása
        treeItemContacts.setExpanded(true);

        // Képek Node-kénti beimportálása
        Node contactNode = new ImageView(new Image(getClass().getResourceAsStream("contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("export.png")));

        // Contacts menü almenüinek létrehozása és képek hozzáfűzése
        TreeItem<String> treeItemContactsList = new TreeItem<>(MENU_LIST, contactNode);
        TreeItem<String> treeItemContactsExports = new TreeItem<>(MENU_EXPORTS, exportNode);

        // Menü részek összefűzése
        treeItemContacts.getChildren().addAll(treeItemContactsList, treeItemContactsExports);
        treeItemRoot.getChildren().addAll(treeItemContacts, treeItemExit);

        // Menü rendszer felhelyezése a menuPane-re
        menuPane.getChildren().add(treeView);

        // Egykattintásos legördítés megvalósítása
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu = selectedItem.getValue();
                if (selectedMenu != null) {
                    switch (selectedMenu) {
                        case MENU_CONTACTS:
                            try {
                            treeItemContacts.setExpanded(true);
                        } catch (Exception ex) {
                        }
                        break;
                        case MENU_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case MENU_EXPORTS:
                            contactPane.setVisible(false);
                            exportPane.setVisible(true);
                            break;
                        case MENU_EXIT:
                            System.exit(0);
                            break;
                    }
                }
            }
        });
    }

}
