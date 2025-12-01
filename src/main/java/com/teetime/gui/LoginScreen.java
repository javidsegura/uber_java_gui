package com.teetime.gui;

import com.teetime.domain.User;
import com.teetime.exception.InvalidLoginException;
import com.teetime.service.AuthService;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScreen {
    private VBox view;
    private Stage stage;
    private HostServices hostServices;
    private AuthService authService;

    public LoginScreen(Stage stage, HostServices hostServices) {
        this.stage = stage;
        this.hostServices = hostServices;
        this.authService = new AuthService();
        createView();
    }

    private void createView() {
        view = new VBox(15);
        view.setPadding(new Insets(40));
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("TeeTime");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("Campus Ride Sharing");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");

        // Login form
        GridPane loginForm = new GridPane();
        loginForm.setHgap(10);
        loginForm.setVgap(10);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(400);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("your.email@student.ie.edu");
        emailField.setMaxWidth(300);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMaxWidth(300);

        loginForm.add(emailLabel, 0, 0);
        loginForm.add(emailField, 1, 0);
        loginForm.add(passwordLabel, 0, 1);
        loginForm.add(passwordField, 1, 1);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        loginButton.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        registerButton.setOnAction(e -> showRegistrationDialog());

        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Message label
        Label messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");

        view.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Region(),
            loginForm,
            buttonBox,
            messageLabel
        );

        // Allow Enter key to login
        passwordField.setOnAction(e -> handleLogin(emailField.getText(), passwordField.getText()));
    }

    private void handleLogin(String email, String password) {
        try {
            User user = authService.login(email, password);
            openDashboard(user);
        } catch (InvalidLoginException e) {
            showAlert("Login Failed", e.getMessage());
        }
    }

    private void showRegistrationDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register New User");
        dialog.setHeaderText("Create your TeeTime account");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("email@student.ie.edu");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("PASSENGER", "DRIVER", "BOTH");
        roleCombo.setValue("PASSENGER");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == registerButtonType) {
                try {
                    User user = authService.register(
                        nameField.getText(),
                        emailField.getText(),
                        passwordField.getText(),
                        roleCombo.getValue()
                    );
                    showAlert("Success", "Registration successful! You can now login.");
                } catch (Exception e) {
                    showAlert("Registration Failed", e.getMessage());
                }
            }
        });
    }

    private void openDashboard(User user) {
        if (user.getRole().contains("DRIVER")) {
            DriverDashboard dashboard = new DriverDashboard(stage, user, hostServices);
            Scene scene = new Scene(dashboard.getView(), 1000, 700);
            stage.setScene(scene);
        } else {
            PassengerDashboard dashboard = new PassengerDashboard(stage, user, hostServices);
            Scene scene = new Scene(dashboard.getView(), 900, 600);
            stage.setScene(scene);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}

