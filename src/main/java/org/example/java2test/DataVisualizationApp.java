package org.example.java2test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class DataVisualizationApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent selectionSceneRoot = FXMLLoader.load(getClass().getResource("/org/example/fxml/selection.fxml"));
        primaryStage.setTitle("Currencies Selection");
        primaryStage.setScene(new Scene(selectionSceneRoot, 400, 300));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
