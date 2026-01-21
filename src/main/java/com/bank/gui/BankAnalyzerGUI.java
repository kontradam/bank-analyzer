package com.bank.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.bank.model.Transaction;
import com.bank.service.*;
import java.io.File;
import java.util.*;

public class BankAnalyzerGUI extends Application {

    private TableView<Transaction> transactionTable;
    private Label summaryLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bank CSV Analyzer");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Button loadButton = new Button("Load CSV Files");
        loadButton.setOnAction(e -> loadFiles(primaryStage));

        HBox topBar = new HBox(10);
        topBar.getChildren().add(loadButton);

        summaryLabel = new Label("No files loaded");

        root.setTop(topBar);
        root.setCenter(new Label("Transaction table will appear here"));
        root.setBottom(summaryLabel);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFiles(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV Files");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null && !files.isEmpty()) {
            processFiles(files);
        }
    }

    private void processFiles(List<File> files) {
        summaryLabel.setText("Processing " + files.size() + " files...");
        // TODO: Implement file processing
    }
}
