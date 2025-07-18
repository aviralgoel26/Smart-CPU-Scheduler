# Java CPU Scheduler Simulator

This is a Java-based CPU scheduling simulator that provides a graphical user interface to visualize and compare different scheduling algorithms. The project is a complete conversion of an original Python application, enhanced with a more user-friendly interface and improved visualizations.

## Features

*   **Multiple Scheduling Algorithms:** Supports First-Come, First-Served (FCFS), Shortest-Job-First (SJF), and Round Robin scheduling.
*   **Interactive UI:** Allows users to add, edit, and delete processes with custom arrival times, burst times, and priorities.
*   **Gantt Chart Visualization:** Displays a dynamic Gantt chart that visualizes the CPU execution sequence for the selected algorithm.
*   **Performance Metrics:** Calculates and displays the average waiting time and average turnaround time for each simulation.
*   **Modern Look and Feel:** Uses the Nimbus look and feel for a clean and modern UI, with a visually appealing color theme for the Gantt chart.
*   **Flexible Process IDs:** Supports string-based process IDs (e.g., "P1", "P2") for better identification.

## Requirements

*   Java Development Kit (JDK) 8 or higher.

## How to Compile and Run

1.  **Navigate to the project directory** in your terminal:
    ```sh
    cd path\to\cpu-scheduler-simulator
    ```

2.  **Compile the Java source files**:
    ```sh
    javac -d java-cpu-scheduler-simulator/bin java-cpu-scheduler-simulator/src/*.java
    ```

3.  **Run the application**:
    ```sh
    java -cp java-cpu-scheduler-simulator/bin Main
    ```

This will launch the CPU Scheduler Simulator, and you can begin adding processes and running simulations.
