package com.cpuscheduler.core;

import com.cpuscheduler.model.Process;
import java.util.*;

/**
 * First-Come, First-Served (FCFS) CPU scheduling algorithm.
 * Processes are executed in the order they arrive.
 */
public class FCFSScheduler extends AbstractScheduler {

    @Override
    public String getName() {
        return "First-Come, First-Served (FCFS)";
    }

    @Override
    public List<String> execute() {
        // Reset state
        ganttChart.clear();
        completedProcesses.clear();
        currentTime = 0;
        contextSwitches = 0;

        // Sort processes by arrival time
        sortByArrivalTime();
        Queue<Process> readyQueue = new LinkedList<>(processes);

        while (!allProcessesCompleted()) {
            // Get the next process to execute
            Process currentProcess = null;
            for (Process p : readyQueue) {
                if (p.getArrivalTime() <= currentTime && !p.isCompleted()) {
                    currentProcess = p;
                    break;
                }
            }

            if (currentProcess == null) {
                // No process is ready to execute, increment time
                currentTime++;
                continue;
            }

            // Execute the process until completion
            int startTime = currentTime;
            int executionTime = currentProcess.getBurstTime();
            currentTime += executionTime;
            
            // Update process completion
            currentProcess.execute(executionTime, startTime);
            completedProcesses.add(currentProcess);
            
            // Update Gantt chart
            updateGanttChart(currentProcess, startTime, currentTime);
            
            // Count context switch (only if there are more processes to come)
            if (!allProcessesCompleted()) {
                contextSwitches++;
            }
        }

        return ganttChart;
    }
}
