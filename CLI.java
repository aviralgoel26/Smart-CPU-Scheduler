package com.cpuscheduler.ui;

import com.cpuscheduler.core.*;
import com.cpuscheduler.core.SchedulerFactory.SchedulerType;
import com.cpuscheduler.model.Process;
import java.io.Console;
import java.util.*;

/**
 * Command-line interface for the CPU Scheduler application.
 */
public class CLI {
    private final Scanner scanner;
    private final List<Process> processes;
    private final Map<Integer, String> schedulerMap;
    private final Console console;

    public CLI() {
        this.console = System.console();
        this.scanner = new Scanner(System.in);
        this.processes = new ArrayList<>();
        this.schedulerMap = new HashMap<>();
        initializeSchedulerMap();
    }
    
    /**
     * Reads a line of text from the console or standard input.
     * @param prompt The prompt to display.
     * @return The user's input, or null if input was cancelled.
     */
    private String readLine(String prompt) {
        if (console != null) {
            return console.readLine("%s", prompt);
        } else {
            System.out.print(prompt);
            System.out.flush();
            try {
                return scanner.nextLine();
            } catch (NoSuchElementException e) {
                return null; // Input was cancelled (Ctrl+D or similar)
            }
        }
    }

    private void initializeSchedulerMap() {
        schedulerMap.put(1, "First-Come, First-Served (FCFS)");
        schedulerMap.put(2, "Shortest Job First (SJF)");
        schedulerMap.put(3, "Priority Scheduling");
        schedulerMap.put(4, "Round Robin");
    }

    public void start() {
        System.out.println("=== CPU Scheduler Simulator ===\n");
        
        try {
            // Input processes
            inputProcesses();
            
            // Select scheduling algorithm
            Scheduler scheduler = selectScheduler();
            
            // Run the scheduler and display results
            runScheduler(scheduler);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private void inputProcesses() {
        System.out.println("=== Process Input ===");
        System.out.println("Enter process details in one of these formats:");
        System.out.println("1. name,arrival,burst,priority");
        System.out.println("2. name, arrival, burst, priority");
        System.out.println("3. name arrival burst priority");
        System.out.println("Note: Priority is optional (default=1). Example: P1,0,5,1 or P1 0 5 1");
        System.out.println("Type 'sample' to load sample processes or 'done' when finished.\n");
        
        int processId = 1;
        while (true) {
            // Add sample processes if requested
            if (processId == 1) {
                String choice = readLine("Load sample processes? (y/n): ").trim().toLowerCase();
                if (choice.equals("y") || choice.equals("yes") || choice.equals("sample")) {
                    // Add some sample processes
                    processes.add(new Process(1, "P1", 0, 5, 2));
                    processes.add(new Process(2, "P2", 1, 3, 1));
                    processes.add(new Process(3, "P3", 2, 8, 3));
                    processes.add(new Process(4, "P4", 3, 6, 2));
                    System.out.println("\nAdded 4 sample processes:");
                    System.out.println("P1: Arrival=0, Burst=5, Priority=2");
                    System.out.println("P2: Arrival=1, Burst=3, Priority=1");
                    System.out.println("P3: Arrival=2, Burst=8, Priority=3");
                    System.out.println("P4: Arrival=3, Burst=6, Priority=2\n");
                    return;
                }
            }
            String input = readLine("Process " + processId + " (or 'done' to finish): ").trim();
            
            // Check if input was cancelled (Ctrl+Z on Windows, Ctrl+D on Unix)
            if (input == null) {
                System.out.println("\nInput cancelled. Exiting...");
                System.exit(0);
            }
            
            if (input.equalsIgnoreCase("done")) {
                if (processes.isEmpty()) {
                    System.out.println("Please enter at least one process.");
                    continue;
                }
                break;
            }
            
            // Skip empty lines
            if (input.isEmpty()) {
                continue;
            }
            
            try {
                // Split by commas first, then trim each part
                String[] parts = input.split(",");
                if (parts.length < 3 || parts.length > 4) {
                    System.out.println("Invalid input. Please enter: name, arrival, burst, [priority]");
                    continue;
                }
                
                // Trim each part and handle potential quotes
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim().replaceAll("^\"|\"$", "");
                }
                
                String name = parts[0];
                int arrivalTime = Integer.parseInt(parts[1]);
                int burstTime = Integer.parseInt(parts[2]);
                int priority = (parts.length == 4) ? Integer.parseInt(parts[3]) : 1; // Default priority is 1
                
                if (name.isEmpty()) {
                    System.out.println("Process name cannot be empty");
                    continue;
                }
                
                if (arrivalTime < 0) {
                    System.out.println("Arrival time must be >= 0");
                    continue;
                }
                
                if (burstTime <= 0) {
                    System.out.println("Burst time must be > 0");
                    continue;
                }
                
                if (priority <= 0) {
                    System.out.println("Priority must be > 0");
                    continue;
                }
                
                Process process = new Process(processId, name, arrivalTime, burstTime, priority);
                processes.add(process);
                processId++;
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    private Scheduler selectScheduler() {
        System.out.println("\n=== Select Scheduling Algorithm ===");
        for (Map.Entry<Integer, String> entry : schedulerMap.entrySet()) {
            System.out.printf("%d. %s%n", entry.getKey(), entry.getValue());
        }
        
        while (true) {
            try {
                String choiceStr = readLine("\nEnter your choice (1-4): ").trim();
                if (choiceStr.isEmpty()) {
                    System.out.println("Please enter a number between 1 and 4.");
                    continue;
                }
                int choice = Integer.parseInt(choiceStr);
                
                if (choice < 1 || choice > 4) {
                    System.out.println("Please enter a number between 1 and 4.");
                    continue;
                }
                
                int timeQuantum = 1;
                if (choice == 4) { // Round Robin
                    String input = readLine("Enter time quantum (default=4): ").trim();
                    if (input != null && !input.isEmpty()) {
                        timeQuantum = Integer.parseInt(input);
                        if (timeQuantum <= 0) {
                            System.out.println("Time quantum must be greater than 0. Using default value of 4.");
                            timeQuantum = 4;
                        }
                    } else {
                        timeQuantum = 4;
                    }
                }
                
                SchedulerType type = SchedulerType.values()[choice - 1];
                return SchedulerFactory.createScheduler(type, timeQuantum);
                
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void runScheduler(Scheduler scheduler) {
        // Add all processes to the scheduler
        for (Process process : processes) {
            scheduler.addProcess(process);
        }
        
        // Run the scheduler
        System.out.println("\n=== Running " + scheduler.getName() + " ===");
        List<String> ganttChart = scheduler.execute();
        
        // Display results
        System.out.println("\n=== Gantt Chart ===");
        for (String entry : ganttChart) {
            System.out.println(entry);
        }
        
        System.out.println("\n=== Performance Metrics ===");
        System.out.printf("Average Waiting Time: %.2f%n", scheduler.getAverageWaitingTime());
        System.out.printf("Average Turnaround Time: %.2f%n", scheduler.getAverageTurnaroundTime());
        System.out.printf("Average Response Time: %.2f%n", scheduler.getAverageResponseTime());
        System.out.println("Number of Context Switches: " + scheduler.getContextSwitches());
        
        // Display process details
        System.out.println("\n=== Process Details ===");
        System.out.println("ID\tName\tArrival\tBurst\tPriority\tCompletion\tWaiting\tTurnaround\tResponse");
        for (Process p : processes) {
            System.out.printf("%d\t%s\t%d\t%d\t%d\t\t%d\t\t%d\t\t%d\t\t%d%n",
                    p.getId(),
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority(),
                    p.getCompletionTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime(),
                    p.getResponseTime());
        }
    }

    public static void main(String[] args) {
        new CLI().start();
    }
}
