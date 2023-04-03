package org.example.java2test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;


public class DataVisualizationController {

    @FXML
    private TableView<DataEntry> dataTable;

    @FXML
    private LineChart<String, Double> lineChart;

    private String currency1;
    private String currency2;
    private LocalDate startDate;
    private LocalDate endDate;

    private Database database;

    private ObservableList<DataEntry> data;

    @FXML
    private DatePicker startProfitDatePicker;

    @FXML
    private DatePicker endProfitDatePicker;

    public void initialize() {
        database = new Database();

        // Set min/max dates for DatePicker controls
        startProfitDatePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #b1b1b3;");
                }
                if (item.isBefore(startDate) || item.isAfter(endDate)) {
                    setDisable(true);
                }
            }
        });
        endProfitDatePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #b1b1b3;");
                }
                if (item.isBefore(startDate) || item.isAfter(endDate)) {
                    setDisable(true);
                }
            }
        });
    }




    public void setValues(String currency1, String currency2, LocalDate startDate, LocalDate endDate) {
        this.currency1 = currency1;
        this.currency2 = currency2;
        this.startDate = startDate;
        this.endDate = endDate;
        updateData();
    }

    private void updateData() {
        fetchData();
        populateTable(currency1,currency2);
    }



    private void populateTable(String currency1, String currency2) {
        TableColumn<DataEntry, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<DataEntry, Double> currency1ValueColumn = new TableColumn<>(currency1);
        currency1ValueColumn.setCellValueFactory(new PropertyValueFactory<>("currency1Value"));

        TableColumn<DataEntry, Double> currency2ValueColumn = new TableColumn<>(currency2);
        currency2ValueColumn.setCellValueFactory(new PropertyValueFactory<>("currency2Value"));

        dataTable.getColumns().addAll(dateColumn, currency1ValueColumn, currency2ValueColumn);

        // Initialize the data field with both currency values
        data = FXCollections.observableArrayList(database.getDataEntries(currency1, currency2));

        // Filter data based on the date range
        ObservableList<DataEntry> filteredData = filterDataByCurrencyAndDate(startDate, endDate);

        dataTable.setItems(filteredData);
    }


    private void updateChart(ObservableList<DataEntry> filteredData1, ObservableList<DataEntry> filteredData2) {
        XYChart.Series<String, Double> series1 = new XYChart.Series<>();
        series1.setName(currency1);
        for (DataEntry entry : filteredData1) {
            series1.getData().add(new XYChart.Data<>(entry.getDate().split(" ")[0], entry.getCurrency1Value()));
        }

        XYChart.Series<String, Double> series2 = new XYChart.Series<>();
        series2.setName(currency2);
        for (DataEntry entry : filteredData2) {
            series2.getData().add(new XYChart.Data<>(entry.getDate().split(" ")[0], entry.getCurrency2Value()));
        }

        lineChart.getData().clear();
        lineChart.getData().addAll(series1, series2);
    }


    private void fetchData() {
        if (currency1 == null || currency2 == null) {
            System.out.println("Please select currencies, start date, and end date.");
            return;
        }
        data = FXCollections.observableArrayList(database.getDataEntries(currency1, currency2));
        ObservableList<DataEntry> filteredData1 = filterDataByCurrencyAndDate(startDate, endDate);
        ObservableList<DataEntry> filteredData2 = filterDataByCurrencyAndDate(startDate, endDate);

        dataTable.setItems(filteredData1);
        updateChart(filteredData1, filteredData2);
    }

    private ObservableList<DataEntry> filterDataByCurrencyAndDate(LocalDate startDate, LocalDate endDate) {
        ObservableList<DataEntry> filteredData = FXCollections.observableArrayList();
        for (DataEntry entry : data) {
            LocalDate entryDate = LocalDate.parse(entry.getDate());

            if (!entryDate.isBefore(startDate) && !entryDate.isAfter(endDate)) {
                filteredData.add(entry);
            }
        }
        return filteredData;
    }

    private void calculateProfit(LocalDate startDate, LocalDate endDate) {
        ObservableList<DataEntry> filteredData = filterDataByCurrencyAndDate(startDate, endDate);

        if (filteredData.size() < 2) {
            return;
        }

        DataEntry startEntry = filteredData.get(0);
        DataEntry endEntry = filteredData.get(filteredData.size() - 1);

        double startRate = startEntry.getCurrency1Value() / startEntry.getCurrency2Value();
        double endRate = endEntry.getCurrency1Value() / endEntry.getCurrency2Value();

        double profit = (endRate - startRate) * 100;

        showAlert(currency1 + " : " + currency2, String.format("%.3f",profit) + "%");
    }


    public void calculateProfitBTN(javafx.event.ActionEvent actionEvent) {
        calculateProfit(startProfitDatePicker.getValue(), endProfitDatePicker.getValue());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}