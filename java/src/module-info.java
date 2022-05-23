module plans {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.headroyce.lross2024 to javafx.fxml;
    exports org.headroyce.lross2024;
}