package com.cpuscheduler.core;

import com.cpuscheduler.model.Process;
import java.util.List;

/**
 * Interface for CPU scheduling algorithms.
 */
public interface Scheduler {
    /**
     * Adds a process to the scheduler.
     * @param process The process to be added.
     */
    void addProcess(Process process);

    /**
     * Executes the scheduling algorithm.
     * @return A list of strings representing the Gantt chart.
     */
    List<String> execute();

    /**
     * Gets the name of the scheduling algorithm.
     * @return The name of the scheduler.
     */
    String getName();

    /**
     * Gets the average waiting time of all processes.
     * @return The average waiting time.
     */
    double getAverageWaitingTime();

    /**
     * Gets the average turnaround time of all processes.
     * @return The average turnaround time.
     */
    double getAverageTurnaroundTime();

    /**
     * Gets the average response time of all processes.
     * @return The average response time.
     */
    double getAverageResponseTime();

    /**
     * Gets the total number of context switches that occurred during scheduling.
     * @return The number of context switches.
     */
    int getContextSwitches();
}
