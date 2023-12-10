module com.phonebook {
    requires javafx.controls;
    requires javafx.fxml;
     requires itextpdf;

    opens com.phonebook to javafx.fxml;
    exports com.phonebook;
}
