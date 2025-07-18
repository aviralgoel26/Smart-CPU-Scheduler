package com.cpuscheduler.core;

import com.cpuscheduler.model.Process;
import java.util.*;

/**
 * Round Robin (RR) CPU scheduling algorithm.
 * Each process is assigned a fixed time slot (time quantum) in a cyclic way.
 */
public class RoundRobinScheduler extends AbstractScheduler {
    private final int timeQuantum;

    /**
     * Creates a Round Robin scheduler with the specified time quantum.
     * @param timeQuantum The time quantum for the scheduler.
     */
    public RoundRobinScheduler(int timeQuantum) {
        if (timeQuantum <= 0) {
            throw new IllegalArgumentException("Time quantum must be greater than 0");
        }
        this.timeQuantum = timeQuantum;
    }

    @Override
    public String getName() {
        return String.format("Round Robin (Time Quantum = %d)", timeQuantum);
    }

    @Override
    public List<String> execute() {
        // Reset state
        ganttChart.clear();
        completedProcesses.clear();
        currentTime = 0;
        contextSwitches = 0;

        // Make a copy of processes and sort by arrival time
        List<Process> processList = new ArrayList<>(processes);
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Queue for ready processes
        Queue<Process> readyQueue = new LinkedList<>();
        int nextProcessIndex = 0;
        Process currentProcess = null;
        int remainingTimeInQuantum = 0;

        while (!allProcessesCompleted()) {
            // Add newly arrived processes to the ready queue
            while (nextProcessIndex < processList.size()) {
                Process p = processList.get(nextProcessIndex);
                if (p.getArrivalTime() <= currentTime) {
                    readyQueue.add(p);
                    nextProcessIndex++;
                } else {
                    break;
                }
            }

            // If the current process has completed its time quantum or has finished execution
            if (currentProcess != null && (remainingTimeInQuantum == 0 || currentProcess.isCompleted())) {
                if (!currentProcess.isCompleted()) {
                    // Add the current process back to the ready queue if it's not completed
                    readyQueue.add(currentProcess);
                } else {
                    // Process has completed execution
                    completedProcesses.add(currentProcess);
                }
                currentProcess = null;
            }

            // If no process is currently running, get the next one from the ready queue
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                remainingTimeInQuantum = timeQuantum;
                
                // Count context switch (except for the first process)
                if (currentTime > 0) {
                    contextSwitches++;
                }
            }

            if (currentProcess != null) {
                // Execute the current process for one time unit
                int executionTime = Math.min(1, currentProcess.getRemainingTime());
                int startTime = currentTime;
                
                currentProcess.execute(executionTime, currentTime);
                remainingTimeInQuantum--;
                
                // Update Gantt chart
                updateGanttChart(currentProcess, startTime, currentTime + 1);
                
                currentTime++;
            } else {
                // No process is ready to execute, increment time
                currentTime++;
            }
        }

        return ganttChart;
    }
}
