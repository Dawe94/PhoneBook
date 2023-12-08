module com.phonebook {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.phonebook to javafx.fxml;
    exports com.phonebook;
}
