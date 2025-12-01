package com.teetime;

import com.teetime.gui.LoginScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TeeTime - Campus Ride Sharing");
        
        LoginScreen loginScreen = new LoginScreen(primaryStage, getHostServices());
        Scene scene = new Scene(loginScreen.getView(), 800, 600);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

