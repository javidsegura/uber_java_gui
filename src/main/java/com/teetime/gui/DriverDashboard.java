package com.teetime.gui;

import com.teetime.domain.Car;
import com.teetime.domain.Ride;
import com.teetime.domain.RideStatus;
import com.teetime.domain.User;
import com.teetime.service.CSVExportService;
import com.teetime.service.RideService;
import javafx.application.HostServices;
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
import java.util.Optional;

public class DriverDashboard {
    private BorderPane view;
    private Stage stage;
    private User user;
    private HostServices hostServices;
    private RideService rideService;
    private CSVExportService csvService;
    private TableView<Car> carsTable;
    private TableView<Ride> pendingRidesTable;
    private TableView<Ride> myRidesTable;
    private ObservableList<Car> cars;
    private ObservableList<Ride> pendingRides;
    private ObservableList<Ride> myRides;

    public DriverDashboard(Stage stage, User user, HostServices hostServices) {
        this.stage = stage;
        this.user = user;
        this.hostServices = hostServices;
        this.rideService = new RideService();
        this.csvService = new CSVExportService();
        this.cars = FXCollections.observableArrayList();
        this.pendingRides = FXCollections.observableArrayList();
        this.myRides = FXCollections.observableArrayList();
        createView();
        loadData();
    }

    private void createView() {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #ecf0f1;");

        // Top bar
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #e67e22;");

        Label titleLabel = new Label("Driver Dashboard");
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

        // Center - Tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab carsTab = new Tab("My Cars", createCarsTab());
        Tab requestsTab = new Tab("Available Requests", createRequestsTab());
        Tab myRidesTab = new Tab("My Accepted Rides", createMyRidesTab());
        Tab rideMapTab = new Tab("Ride Map", createRideMapTab());

        tabPane.getTabs().addAll(carsTab, requestsTab, myRidesTab, rideMapTab);
        view.setCenter(tabPane);
    }

    private VBox createCarsTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        // Add car form
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Label formTitle = new Label("Add New Car");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        TextField plateField = new TextField();
        plateField.setPromptText("ABC-1234");
        TextField brandField = new TextField();
        brandField.setPromptText("Toyota Corolla");
        Spinner<Integer> seatsSpinner = new Spinner<>(1, 8, 4);

        formGrid.add(new Label("Plate:"), 0, 0);
        formGrid.add(plateField, 1, 0);
        formGrid.add(new Label("Brand:"), 0, 1);
        formGrid.add(brandField, 1, 1);
        formGrid.add(new Label("Seats:"), 0, 2);
        formGrid.add(seatsSpinner, 1, 2);

        Button addButton = new Button("Add Car");
        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
        addButton.setOnAction(e -> addCar(plateField.getText(), brandField.getText(), seatsSpinner.getValue()));

        formBox.getChildren().addAll(formTitle, formGrid, addButton);

        // Cars table
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(15));
        tableBox.setStyle("-fx-background-color: white; -fx-background-radius: 5;");

        Label tableTitle = new Label("My Cars");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        carsTable = new TableView<>();
        carsTable.setItems(cars);

        TableColumn<Car, String> plateCol = new TableColumn<>("Plate");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("plate"));

        TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Car, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));

        TableColumn<Car, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Car, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    deleteCar(car);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        carsTable.getColumns().addAll(plateCol, brandCol, seatsCol, actionCol);

        tableBox.getChildren().addAll(tableTitle, carsTable);

        box.getChildren().addAll(formBox, tableBox);
        return box;
    }

    private VBox createRequestsTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label title = new Label("Available Ride Requests");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        pendingRidesTable = new TableView<>();
        pendingRidesTable.setItems(pendingRides);

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

        TableColumn<Ride, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Ride, Void>() {
            private final Button acceptButton = new Button("Accept");

            {
                acceptButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                acceptButton.setOnAction(e -> {
                    Ride ride = getTableView().getItems().get(getIndex());
                    acceptRide(ride);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acceptButton);
                }
            }
        });

        pendingRidesTable.getColumns().addAll(originCol, destCol, timeCol, seatsCol, priceCol, actionCol);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadPendingRides());

        box.getChildren().addAll(title, pendingRidesTable, refreshButton);
        return box;
    }

    private VBox createMyRidesTab() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));

        Label title = new Label("My Accepted Rides");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        myRidesTable = new TableView<>();
        myRidesTable.setItems(myRides);

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

        TableColumn<Ride, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<Ride, Void>() {
            private final Button completeButton = new Button("Complete");

            {
                completeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                completeButton.setOnAction(e -> {
                    Ride ride = getTableView().getItems().get(getIndex());
                    completeRide(ride);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Ride ride = getTableView().getItems().get(getIndex());
                    if (ride.getStatus() == RideStatus.CONFIRMED) {
                        setGraphic(completeButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        myRidesTable.getColumns().addAll(originCol, destCol, timeCol, seatsCol, statusCol, priceCol, actionCol);

        HBox buttons = new HBox(10);
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadMyRides());
        
        Button exportButton = new Button("Export to CSV");
        exportButton.setOnAction(e -> exportToCSV());
        
        buttons.getChildren().addAll(refreshButton, exportButton);

        box.getChildren().addAll(title, myRidesTable, buttons);
        return box;
    }

    private void addCar(String plate, String brand, int seats) {
        try {
            rideService.addCar(user.getId(), plate, brand, seats);
            showAlert("Success", "Car added successfully!");
            loadCars();
        } catch (Exception e) {
            showAlert("Error", "Failed to add car: " + e.getMessage());
        }
    }

    private void deleteCar(Car car) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete car " + car.getPlate() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            rideService.deleteCar(car.getId());
            loadCars();
        }
    }

    private void acceptRide(Ride ride) {
        if (cars.isEmpty()) {
            showAlert("Error", "Please add a car first!");
            return;
        }

        // Show dialog to select car
        ChoiceDialog<Car> dialog = new ChoiceDialog<>(cars.get(0), cars);
        dialog.setTitle("Select Car");
        dialog.setHeaderText("Accept ride with which car?");
        dialog.setContentText("Choose car:");

        Optional<Car> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                rideService.acceptRide(ride, user.getId(), result.get().getId());
                showAlert("Success", "Ride accepted!");
                loadPendingRides();
                loadMyRides();
            } catch (Exception e) {
                showAlert("Error", "Failed to accept ride: " + e.getMessage());
            }
        }
    }

    private void completeRide(Ride ride) {
        try {
            rideService.completeRide(ride);
            showAlert("Success", "Ride completed!");
            loadMyRides();
        } catch (Exception e) {
            showAlert("Error", "Failed to complete ride: " + e.getMessage());
        }
    }

    private void loadData() {
        loadCars();
        loadPendingRides();
        loadMyRides();
    }

    private void loadCars() {
        List<Car> userCars = rideService.getCarsByDriverId(user.getId());
        cars.setAll(userCars);
    }

    private void loadPendingRides() {
        List<Ride> pending = rideService.getPendingRides();
        pendingRides.setAll(pending);
    }

    private void loadMyRides() {
        List<Ride> rides = rideService.getRidesByDriverId(user.getId());
        myRides.setAll(rides);
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Rides to CSV");
        fileChooser.setInitialFileName("my_driver_rides.csv");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                csvService.exportRidesToCSV(myRides, file.getAbsolutePath());
                showAlert("Success", "Rides exported to " + file.getName());
            } catch (Exception e) {
                showAlert("Error", "Failed to export: " + e.getMessage());
            }
        }
    }

    private void logout() {
        LoginScreen loginScreen = new LoginScreen(stage, hostServices);
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

    private RideMapPane createRideMapTab() {
        return new RideMapPane(
            "Accepted & completed rides",
            myRides,
            ride -> ride.getStatus() == RideStatus.CONFIRMED || ride.getStatus() == RideStatus.COMPLETED,
            "Accept a ride to see it here.",
            hostServices
        );
    }

    public BorderPane getView() {
        return view;
    }
}

