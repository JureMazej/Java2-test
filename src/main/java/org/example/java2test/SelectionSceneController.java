package org.example.java2test;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class SelectionSceneController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> currency1ComboBox;

    @FXML
    private ComboBox<String> currency2ComboBox;

    @FXML
    private Button submitButton;

    private Database database;


    @FXML
    public void initialize() {
        database = new Database();

        // Set min/max dates for DatePicker controls
        startDatePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #b1b1b3;");
                }
                if (item.isBefore(database.getMinDate()) || item.isAfter(database.getMaxDate())) {
                    setDisable(true);
                }
            }
        });
        endDatePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #b1b1b3;");
                }
                if (item.isBefore(database.getMinDate()) || item.isAfter(database.getMaxDate())) {
                    setDisable(true);
                }
            }
        });

        startDatePicker.setValue(database.getMaxDate().minusMonths(1));
        endDatePicker.setValue(database.getMaxDate());

        updateCurrencies();

        // Add listeners to DatePicker controls
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateCurrencies());
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateCurrencies());
    }


    @FXML
    private void handleSubmitButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            String currency1 = currency1ComboBox.getSelectionModel().getSelectedItem();
            String currency2 = currency2ComboBox.getSelectionModel().getSelectedItem();

            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null || currency1ComboBox.getValue() == null || currency2ComboBox.getValue() == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            if (currency1ComboBox.getValue().equals(currency2ComboBox.getValue())) {
                showAlert("Error", "Please select two different currencies.");
                return;
            }

            if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                showAlert("Error", "Please select correct dates.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));
            Parent mainSceneRoot = loader.load();
            DataVisualizationController controller = loader.getController();
            controller.setValues(currency1, currency2, startDatePicker.getValue(), endDatePicker.getValue());

            Stage stage = (Stage) submitButton.getScene().getWindow();
            Scene mainScene = new Scene(mainSceneRoot, 800, 600);
            stage.setTitle("Data view");
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void updateCurrencies() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        List<String> currencies = database.getCurrencies(startDate, endDate);

        currency1ComboBox.setItems(FXCollections.observableArrayList(currencies));
        currency2ComboBox.setItems(FXCollections.observableArrayList(currencies));
    }

}
