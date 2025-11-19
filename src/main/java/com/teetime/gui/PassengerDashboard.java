package com.teetime.gui;

import com.teetime.domain.Ride;
import com.teetime.domain.RideStatus;
import com.teetime.domain.User;
import com.teetime.service.CSVExportService;
import com.teetime.service.RideService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PassengerDashboard {
    private BorderPane view;
    private Stage stage;
    private User user;
    private RideService rideService;
    private CSVExportService csvService;
    private TableView<Ride> ridesTable;
    private ObservableList<Ride> rides;

    public PassengerDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.rideService = new RideService();
        this.csvService = new CSVExportService();
        this.rides = FXCollections.observableArrayList();
        createView();
        loadRides();
    }

    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #ecf0f1;");

        // Top bar
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #3498db;");

        Label titleLabel = new Label("Passenger Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label userLabel = new Label("Welcome, " + user.getName());
        userLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> logout());

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        view.setTop(topBar);

        // Center - Split into form and table
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));

        // Request ride form
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Label formTitle = new Label("Create Ride Request");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        TextField originField = new TextField();
        originField.setPromptText("e.g., Campus Main Gate");
        TextField destField = new TextField();
        destField.setPromptText("e.g., City Center");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("HH:MM (e.g., 14:30)");
        Spinner<Integer> seatsSpinner = new Spinner<>(1, 8, 1);

        formGrid.add(new Label("Origin:"), 0, 0);
        formGrid.add(originField, 1, 0);
        formGrid.add(new Label("Destination:"), 0, 1);
        formGrid.add(destField, 1, 1);
        formGrid.add(new Label("Date:"), 0, 2);
        formGrid.add(datePicker, 1, 2);
        formGrid.add(new Label("Time:"), 0, 3);
        formGrid.add(timeField, 1, 3);
        formGrid.add(new Label("Seats:"), 0, 4);
        formGrid.add(seatsSpinner, 1, 4);

        Button createButton = new Button("Create Request");
        createButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
        createButton.setOnAction(e -> createRideRequest(
            originField.getText(),
            destField.getText(),
            datePicker.getValue(),
            timeField.getText(),
            seatsSpinner.getValue()
        ));

        formBox.getChildren().addAll(formTitle, formGrid, createButton);

        // Rides table
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(15));
        tableBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Label tableTitle = new Label("My Rides");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        ridesTable = new TableView<>();
        ridesTable.setItems(rides);

        TableColumn<Ride, String> originCol = new TableColumn<>("Origin");
        originCol.setCellValueFactory(new PropertyValueFactory<>("origin"));

        TableColumn<Ride, String> destCol = new TableColumn<>("Destination");
        destCol.setCellValueFactory(new PropertyValueFactory<>("destination"));

        TableColumn<Ride, LocalDateTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.setCellFactory(column -> new TableCell<Ride, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        });

        TableColumn<Ride, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("seatsNeeded"));

        TableColumn<Ride, RideStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Ride, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("priceEstimate"));
        priceCol.setCellFactory(column -> new TableCell<Ride, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", item));
                }
            }
        });

        ridesTable.getColumns().addAll(originCol, destCol, timeCol, seatsCol, statusCol, priceCol);

        HBox tableButtons = new HBox(10);
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadRides());
        
        Button exportButton = new Button("Export to CSV");
        exportButton.setOnAction(e -> exportToCSV());
        
        tableButtons.getChildren().addAll(refreshButton, exportButton);

        tableBox.getChildren().addAll(tableTitle, ridesTable, tableButtons);

        centerBox.getChildren().addAll(formBox, tableBox);
        view.setCenter(new ScrollPane(centerBox));
    }

    private void createRideRequest(String origin, String dest, java.time.LocalDate date, String time, int seats) {
        try {
            // Parse time
            String[] timeParts = time.split(":");
            if (timeParts.length != 2) {
                throw new Exception("Invalid time format. Use HH:MM");
            }
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            
            LocalDateTime dateTime = date.atTime(hour, minute);
            
            rideService.createRideRequest(user.getId(), origin, dest, dateTime, seats);
            showAlert("Success", "Ride request created successfully!");
            loadRides();
        } catch (Exception e) {
            showAlert("Error", "Failed to create ride: " + e.getMessage());
        }
    }

    private void loadRides() {
        List<Ride> userRides = rideService.getRidesByPassengerId(user.getId());
        rides.setAll(userRides);
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Rides to CSV");
        fileChooser.setInitialFileName("my_rides.csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                csvService.exportRidesToCSV(rides, file.getAbsolutePath());
                showAlert("Success", "Rides exported to " + file.getName());
            } catch (Exception e) {
                showAlert("Error", "Failed to export: " + e.getMessage());
            }
        }
    }

    private void logout() {
        LoginScreen loginScreen = new LoginScreen(stage);
        Scene scene = new Scene(loginScreen.getView(), 800, 600);
        stage.setScene(scene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView() {
        return view;
    }
}

