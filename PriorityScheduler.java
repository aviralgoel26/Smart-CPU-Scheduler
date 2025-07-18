package com.cpuscheduler.core;

import com.cpuscheduler.model.Process;
import java.util.*;

/**
 * Priority Scheduling algorithm (non-preemptive).
 * Processes are executed based on their priority (lower number indicates higher priority).
 */
public class PriorityScheduler extends AbstractScheduler {

    @Override
    public String getName() {
        return "Priority Scheduling - Non-preemptive";
    }

    @Override
    public List<String> execute() {
        // Reset state
        ganttChart.clear();
        completedProcesses.clear();
        currentTime = 0;
        contextSwitches = 0;

        // Make a copy of processes to avoid modifying the original list
        List<Process> remainingProcesses = new ArrayList<>(processes);
        
        // Sort processes by arrival time initially
        remainingProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (!allProcessesCompleted()) {
            // Get all arrived processes that haven't completed yet
            List<Process> arrivedProcesses = new ArrayList<>();
            for (Process p : remainingProcesses) {
                if (p.getArrivalTime() <= currentTime && !p.isCompleted()) {
                    arrivedProcesses.add(p);
                }
            }

            if (arrivedProcesses.isEmpty()) {
                // No process is ready to execute, increment time
                currentTime++;
                continue;
            }

            // Find the process with the highest priority (lowest priority number)
            Process currentProcess = arrivedProcesses.stream()
                    .min(Comparator.comparingInt(Process::getPriority)
                            .thenComparing(Process::getArrivalTime))
                    .orElse(null);

            if (currentProcess != null) {
                // Execute the process until completion (non-preemptive)
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
            } else {
                currentTime++;
            }
        }

        return ganttChart;
    }
}
