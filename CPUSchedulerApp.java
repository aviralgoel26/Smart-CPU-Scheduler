package com.cpuscheduler.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CPUSchedulerApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        
        // Set up the stage
        primaryStage.setTitle("CPU Scheduler Simulator");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        
        // Show the stage
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
