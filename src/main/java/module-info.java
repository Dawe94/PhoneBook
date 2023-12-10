module com.phonebook {
    requires javafx.controls;
    requires javafx.fxml;
     requires itextpdf;
     requires java.sql;

    opens com.phonebook to javafx.fxml;
    exports com.phonebook;
}
