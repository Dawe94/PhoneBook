package com.phonebook;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    
}
