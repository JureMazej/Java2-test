module org.example.java2test {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.xml;
    requires java.net.http;

    opens org.example.java2test to javafx.fxml;
    exports org.example.java2test;
}