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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class PBController implements Initializable {

    private final String MENU_CONTACTS = "Contacts";
    private final String MENU_EXIT = "Exit";
    private final String MENU_LIST = "List";
    private final String MENU_EXPORTS = "Exports";
    private final DB db = new DB();

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
    @FXML
    private AnchorPane anchor;
    @FXML
    private SplitPane splitPane;
//</editor-fold>

    // Adatbázis híján egy mesterséges Lista létrehozása
    private final ObservableList<Person> data = FXCollections.observableArrayList();

    private void alert(String text) {
        splitPane.setDisable(true);
        splitPane.setOpacity(0.4);
        
        Label alertLabel = new Label(text);
        Button alertButton = new Button("Ok");
        alertButton.setMinWidth(60);
        VBox vbox = new VBox(alertLabel, alertButton);
        vbox.setSpacing(10d);
        vbox.setAlignment(Pos.CENTER);
        
        alertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                splitPane.setDisable(false);
                splitPane.setOpacity(1d);
                vbox.setVisible(false);
            }
        });
        
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox, 300d);
        anchor.setLeftAnchor(vbox, 300d);
    }
    
    @FXML
    public void addContact(ActionEvent event) {
        String email = inputEmail.getText();
        if (isEmailAFormatValid(email)) {
            Person person = new Person(inputLastName.getText(), inputFirstName.getText(), email);
            data.add(person);
            db.addContact(person);
            inputLastName.clear();
            inputFirstName.clear();
            inputEmail.clear();
        } else {
            alert("Invalid email address!");
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
        } else {
            alert("Please enter the file name!");
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
        lastNameCol.setMinWidth(130);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        editCommitEvent(lastNameCol, "LastName");

        TableColumn firstNameCol = new TableColumn("FirstName");
        firstNameCol.setMinWidth(130);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        editCommitEvent(firstNameCol, "FirstName");

        TableColumn emailCol = new TableColumn("EmailAddress");
        emailCol.setMinWidth(250);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
        editCommitEvent(emailCol, "Email");
        
        TableColumn removeCol = new TableColumn("Remove");
        removeCol.setMinWidth(100);
        
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory =
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>()
                {
                @Override
                public TableCell call( final TableColumn<Person, String> param )
                {
                    final TableCell<Person, String> cell = new TableCell<>()
                    { 
                        final Button button = new Button("Remove");
                        
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                button.setOnAction((ActionEvent) -> {
                                    Person person = getTableView().getItems().get( getIndex());
                                    data.remove(person);
                                    db.removeContact(person);
                                });
                                setGraphic(button);
                                setText(null);
                            }
                        }
                    };
                    return cell;
                }      
            }; 
        removeCol.setCellFactory(cellFactory);
    

        // Az oszlopok a táblára való felhelyezése
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol);
        
        data.addAll(db.getAllContacts());
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
        Person actualPerson = (Person)t.getTableView().getItems().get(t.getTablePosition().getRow());
        switch (name) {
            case "LastName":           
                actualPerson.setLastName(t.getNewValue());
                break;
            case "FirstName":
               actualPerson.setFirstName(t.getNewValue());
                break;
            case "Email":
                actualPerson.setEmail(t.getNewValue());
                break;
        }
        db.updateContact(actualPerson);
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
