package com.cpuscheduler.gui;

import com.cpuscheduler.core.*;
import com.cpuscheduler.core.SchedulerFactory.SchedulerType;
import com.cpuscheduler.model.Process;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private Spinner<Integer> quantumSpinner;
    @FXML private TableView<Process> processTable;
    @FXML private Pane ganttChartContainer;
    @FXML private Label avgWaitingTimeLabel;
    @FXML private Label avgTurnaroundTimeLabel;
    @FXML private Label avgResponseTimeLabel;
    @FXML private Label contextSwitchesLabel;
    
    private final ObservableList<Process> processes = FXCollections.observableArrayList();
    private final Map<String, SchedulerType> algorithmMap = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupAlgorithmComboBox();
        setupProcessTable();
        setupQuantumSpinner();
    }
    
    private void setupAlgorithmComboBox() {
        // Map display names to scheduler types
        algorithmMap.put("First-Come, First-Served (FCFS)", SchedulerType.FCFS);
        algorithmMap.put("Shortest Job First (SJF) - Non-preemptive", SchedulerType.SJF);
        algorithmMap.put("Priority Scheduling - Non-preemptive", SchedulerType.PRIORITY);
        algorithmMap.put("Round Robin", SchedulerType.ROUND_ROBIN);
        
        // Add items to combo box
        algorithmCombo.getItems().addAll(algorithmMap.keySet());
        algorithmCombo.getSelectionModel().selectFirst();
        
        // Show/hide quantum spinner based on selection
        algorithmCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isRoundRobin = "Round Robin".equals(newVal);
            quantumSpinner.setVisible(isRoundRobin);
            quantumSpinner.setManaged(isRoundRobin);
        });
    }
    
    private void setupProcessTable() {
        // Configure table columns
        TableColumn<Process, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        
        TableColumn<Process, String> nameColumn = new TableColumn<>("Process");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        TableColumn<Process, Integer> arrivalColumn = new TableColumn<>("Arrival");
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asObject());
        
        TableColumn<Process, Integer> burstColumn = new TableColumn<>("Burst");
        burstColumn.setCellValueFactory(cellData -> cellData.getValue().burstTimeProperty().asObject());
        
        TableColumn<Process, Integer> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty().asObject());
        
        TableColumn<Process, Integer> waitingColumn = new TableColumn<>("Waiting");
        waitingColumn.setCellValueFactory(cellData -> cellData.getValue().waitingTimeProperty().asObject());
        
        TableColumn<Process, Integer> turnaroundColumn = new TableColumn<>("Turnaround");
        turnaroundColumn.setCellValueFactory(cellData -> cellData.getValue().turnaroundTimeProperty().asObject());
        
        TableColumn<Process, Integer> responseColumn = new TableColumn<>("Response");
        responseColumn.setCellValueFactory(cellData -> cellData.getValue().responseTimeProperty().asObject());
        
        // Add columns to table
        processTable.getColumns().setAll(
            idColumn, nameColumn, arrivalColumn, burstColumn, priorityColumn,
            waitingColumn, turnaroundColumn, responseColumn
        );
        
        // Set table data
        processTable.setItems(processes);
    }
    
    private void setupQuantumSpinner() {
        // Configure quantum spinner
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 4);
        quantumSpinner.setValueFactory(valueFactory);
        quantumSpinner.setEditable(true);
        
        // Ensure only numbers are entered
        quantumSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantumSpinner.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    @FXML
    private void handleAddProcess() {
        // Create a dialog to add a new process
        Dialog<Process> dialog = new Dialog<>();
        dialog.setTitle("Add Process");
        dialog.setHeaderText("Enter process details");
        
        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create the process form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Process Name");
        Spinner<Integer> arrivalSpinner = new Spinner<>(0, 100, 0);
        Spinner<Integer> burstSpinner = new Spinner<>(1, 100, 5);
        Spinner<Integer> prioritySpinner = new Spinner<>(1, 10, 1);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Arrival Time:"), 0, 1);
        grid.add(arrivalSpinner, 1, 1);
        grid.add(new Label("Burst Time:"), 0, 2);
        grid.add(burstSpinner, 1, 2);
        grid.add(new Label("Priority (1-10):"), 0, 3);
        grid.add(prioritySpinner, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        Platform.runLater(nameField::requestFocus);
        
        // Convert the result to a process when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    name = "P" + (processes.size() + 1);
                }
                return new Process(
                    processes.size() + 1,
                    name,
                    arrivalSpinner.getValue(),
                    burstSpinner.getValue(),
                    prioritySpinner.getValue()
                );
            }
            return null;
        });
        
        Optional<Process> result = dialog.showAndWait();
        result.ifPresent(process -> {
            processes.add(process);
            processTable.refresh();
        });
    }
    
    @FXML
    private void handleRunSimulation() {
        if (processes.isEmpty()) {
            showAlert("No Processes", "Please add at least one process to simulate.");
            return;
        }
        
        try {
            // Get selected algorithm
            String selectedAlgorithm = algorithmCombo.getSelectionModel().getSelectedItem();
            SchedulerType type = algorithmMap.get(selectedAlgorithm);
            
            // Create scheduler
            Scheduler scheduler;
            if (type == SchedulerType.ROUND_ROBIN) {
                int quantum = quantumSpinner.getValue();
                scheduler = SchedulerFactory.createScheduler(type, quantum);
            } else {
                scheduler = SchedulerFactory.createScheduler(type);
            }
            
            // Add processes to scheduler
            processes.forEach(p -> scheduler.addProcess(new Process(p)));
            
            // Run simulation
            List<String> ganttData = scheduler.execute();
            
            // Update UI with results
            updateMetrics(scheduler);
            drawGanttChart(ganttData);
            
            // Refresh table to show calculated values
            processTable.refresh();
            
        } catch (Exception e) {
            showAlert("Simulation Error", "An error occurred during simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleReset() {
        // Clear all data
        processes.clear();
        ganttChartContainer.getChildren().clear();
        
        // Reset labels
        avgWaitingTimeLabel.setText("-");
        avgTurnaroundTimeLabel.setText("-");
        avgResponseTimeLabel.setText("-");
        contextSwitchesLabel.setText("0");
        
        // Reset table
        processTable.refresh();
    }
    
    private void updateMetrics(Scheduler scheduler) {
        avgWaitingTimeLabel.setText(String.format("%.2f", scheduler.getAverageWaitingTime()));
        avgTurnaroundTimeLabel.setText(String.format("%.2f", scheduler.getAverageTurnaroundTime()));
        avgResponseTimeLabel.setText(String.format("%.2f", scheduler.getAverageResponseTime()));
        contextSwitchesLabel.setText(String.valueOf(scheduler.getContextSwitches()));
    }
    
    private void drawGanttChart(List<String> ganttData) {
        ganttChartContainer.getChildren().clear();
        
        if (ganttData == null || ganttData.isEmpty()) {
            ganttChartContainer.getChildren().add(new Label("No Gantt chart data available"));
            return;
        }
        
        double x = 10;
        double y = 20;
        double height = 40;
        double widthPerTimeUnit = 40;
        
        // Draw timeline
        double maxTime = 0;
        for (String entry : ganttData) {
            String[] parts = entry.split(":");
            if (parts.length < 2) continue;
            
            String timeRange = parts[0].replace("Time ", "").trim();
            String[] times = timeRange.split("-");
            if (times.length != 2) continue;
            
            try {
                double endTime = Double.parseDouble(times[1]);
                maxTime = Math.max(maxTime, endTime);
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        
        // Draw Gantt chart
        for (String entry : ganttData) {
            String[] parts = entry.split(":");
            if (parts.length < 2) continue;
            
            String timeRange = parts[0].replace("Time ", "").trim();
            String processName = parts[1].trim();
            
            String[] times = timeRange.split("-");
            if (times.length != 2) continue;
            
            try {
                double startTime = Double.parseDouble(times[0]);
                double endTime = Double.parseDouble(times[1]);
                double duration = endTime - startTime;
                
                // Draw process bar
                Rectangle bar = new Rectangle(
                    x + startTime * widthPerTimeUnit,
                    y,
                    duration * widthPerTimeUnit,
                    height
                );
                
                // Set color based on process name
                if (processName.equals("Idle")) {
                    bar.setFill(Color.LIGHTGRAY);
                } else {
                    // Generate a consistent color based on process name
                    int hash = processName.hashCode();
                    Color color = Color.hsb(Math.abs(hash) % 360, 0.7, 0.9);
                    bar.setFill(color);
                }
                
                bar.setStroke(Color.BLACK);
                bar.setArcWidth(5);
                bar.setArcHeight(5);
                
                // Add tooltip
                Tooltip tooltip = new Tooltip(String.format("%s\nTime: %.1f - %.1f\nDuration: %.1f", 
                    processName, startTime, endTime, duration));
                Tooltip.install(bar, tooltip);
                
                // Add bar to chart
                ganttChartContainer.getChildren().add(bar);
                
                // Add process name label
                Text label = new Text(processName);
                label.setX(x + startTime * widthPerTimeUnit + 5);
                label.setY(y + height / 2 + 5);
                label.setStyle("-fx-font-weight: bold;");
                ganttChartContainer.getChildren().add(label);
                
                // Add time markers
                Text startLabel = new Text(String.format("%.1f", startTime));
                startLabel.setX(x + startTime * widthPerTimeUnit);
                startLabel.setY(y + height + 15);
                ganttChartContainer.getChildren().add(startLabel);
                
                // Add end time marker for the last process
                if (entry.equals(ganttData.get(ganttData.size() - 1))) {
                    Text endLabel = new Text(String.format("%.1f", endTime));
                    endLabel.setX(x + endTime * widthPerTimeUnit - 15);
                    endLabel.setY(y + height + 15);
                    ganttChartContainer.getChildren().add(endLabel);
                }
                
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        
        // Add axis labels
        Text xAxisLabel = new Text("Time");
        xAxisLabel.setX(10);
        xAxisLabel.setY(y + height + 35);
        ganttChartContainer.getChildren().add(xAxisLabel);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
