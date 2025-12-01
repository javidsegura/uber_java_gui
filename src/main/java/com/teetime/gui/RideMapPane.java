package com.teetime.gui;

import com.teetime.domain.Ride;
import com.teetime.domain.RideStatus;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class RideMapPane extends BorderPane {
    private final FilteredList<Ride> filteredRides;
    private final ListView<Ride> rideListView;
    private final Label rideDetailsLabel;
    private final String emptyMessage;
    private final HostServices hostServices;
    private final Button openInMapsButton;

    private static final DateTimeFormatter DISPLAY_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RideMapPane(String headerText,
                       ObservableList<Ride> rides,
                       Predicate<Ride> rideFilter,
                       String emptyMessage,
                       HostServices hostServices) {
        this.emptyMessage = emptyMessage;
        this.hostServices = hostServices;
        this.filteredRides = new FilteredList<>(rides, rideFilter);
        this.rideListView = new ListView<>(filteredRides);
        this.rideDetailsLabel = new Label("Select a ride to view its route.");
        this.openInMapsButton = new Button("Open Route in Maps");

        initializeLayout(headerText);
        initializeListView();
        hookListeners();
    }

    private void initializeLayout(String headerText) {
        setPadding(new Insets(20));

        Label title = new Label(headerText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox leftPane = new VBox(10);
        leftPane.getChildren().addAll(title, rideListView);
        leftPane.setPrefWidth(320);
        leftPane.setPadding(new Insets(0, 15, 0, 0));

        VBox.setVgrow(rideListView, Priority.ALWAYS);

        VBox rightPane = new VBox(15);
        rideDetailsLabel.setWrapText(true);
        rideDetailsLabel.setFont(Font.font("Arial", 14));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        openInMapsButton.setDisable(true);
        openInMapsButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        openInMapsButton.setMaxHeight(35);
        openInMapsButton.setPrefHeight(35);
        openInMapsButton.setMaxWidth(Double.MAX_VALUE);
        openInMapsButton.setOnAction(e -> {
            Ride selected = rideListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openRouteInExternalMaps(selected);
            }
        });

        rightPane.getChildren().addAll(rideDetailsLabel, openInMapsButton);

        setLeft(leftPane);
        setCenter(rightPane);
    }

    private void initializeListView() {
        rideListView.setPlaceholder(new Label(emptyMessage));
        rideListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Ride ride, boolean empty) {
                super.updateItem(ride, empty);
                if (empty || ride == null) {
                    setText(null);
                } else {
                    String statusText = ride.getStatus() == null
                        ? ""
                        : " • " + ride.getStatus().name();
                    setText(String.format(
                        "%s → %s\n%s%s",
                        ride.getOrigin(),
                        ride.getDestination(),
                        ride.getTime().format(DISPLAY_FORMATTER),
                        statusText
                    ));
                }
            }
        });

        rideListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldRide, newRide) -> displayRide(newRide)
        );
    }

    private void hookListeners() {
        filteredRides.addListener((ListChangeListener<Ride>) change -> {
            if (filteredRides.isEmpty()) {
                rideListView.getSelectionModel().clearSelection();
                displayRide(null);
            } else if (rideListView.getSelectionModel().isEmpty()) {
                rideListView.getSelectionModel().selectFirst();
            }
        });
    }

    private void displayRide(Ride ride) {
        if (ride == null) {
            rideDetailsLabel.setText("Select a ride to view its details.");
            openInMapsButton.setDisable(true);
            return;
        }

        rideDetailsLabel.setText(buildRideDetails(ride));
        openInMapsButton.setDisable(false);
    }

    private String buildRideDetails(Ride ride) {
        StringBuilder builder = new StringBuilder();
        builder.append("Origin: ").append(ride.getOrigin()).append("\n");
        builder.append("Destination: ").append(ride.getDestination()).append("\n");
        builder.append("Time: ").append(ride.getTime().format(DISPLAY_FORMATTER)).append("\n");
        builder.append("Seats: ").append(ride.getSeatsNeeded()).append("\n");
        RideStatus status = ride.getStatus();
        if (status != null) {
            builder.append("Status: ").append(status.name());
        }
        return builder.toString();
    }

    private void openRouteInExternalMaps(Ride ride) {
        String url = buildExternalMapsUrl(ride);
        hostServices.showDocument(url);
    }

    private String buildExternalMapsUrl(Ride ride) {
        String origin = URLEncoder.encode(ride.getOrigin(), StandardCharsets.UTF_8);
        String destination = URLEncoder.encode(ride.getDestination(), StandardCharsets.UTF_8);

        // Use Google Maps directions in the system browser so we don't depend on WebView embedding.
        return "https://www.google.com/maps/dir/?api=1&travelmode=driving&origin="
            + origin + "&destination=" + destination;
    }
}

