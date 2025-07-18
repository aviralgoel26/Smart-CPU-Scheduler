package com.cpuscheduler.core;

import com.cpuscheduler.model.Process;
import java.util.*;

/**
 * Abstract base class for CPU scheduling algorithms.
 * Provides common functionality for all schedulers.
 */
public abstract class AbstractScheduler implements Scheduler {
    protected List<Process> processes;
    protected List<Process> completedProcesses;
    protected List<String> ganttChart;
    protected int currentTime;
    protected int contextSwitches;

    public AbstractScheduler() {
        this.processes = new ArrayList<>();
        this.completedProcesses = new ArrayList<>();
        this.ganttChart = new ArrayList<>();
        this.currentTime = 0;
        this.contextSwitches = 0;
    }

    @Override
    public void addProcess(Process process) {
        processes.add(process);
    }

    @Override
    public double getAverageWaitingTime() {
        if (completedProcesses.isEmpty()) {
            return 0;
        }
        return completedProcesses.stream()
                .mapToInt(Process::getWaitingTime)
                .average()
                .orElse(0);
    }

    @Override
    public double getAverageTurnaroundTime() {
        if (completedProcesses.isEmpty()) {
            return 0;
        }
        return completedProcesses.stream()
                .mapToInt(Process::getTurnaroundTime)
                .average()
                .orElse(0);
    }

    @Override
    public double getAverageResponseTime() {
        if (completedProcesses.isEmpty()) {
            return 0;
        }
        return completedProcesses.stream()
                .mapToInt(Process::getResponseTime)
                .average()
                .orElse(0);
    }

    @Override
    public int getContextSwitches() {
        return contextSwitches;
    }

    /**
     * Gets the list of processes that have arrived by the current time.
     * @return List of arrived processes.
     */
    protected List<Process> getArrivedProcesses() {
        List<Process> arrived = new ArrayList<>();
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && !p.isCompleted() && !completedProcesses.contains(p)) {
                arrived.add(p);
            }
        }
        return arrived;
    }

    /**
     * Checks if all processes have completed execution.
     * @return true if all processes are completed, false otherwise.
     */
    protected boolean allProcessesCompleted() {
        return completedProcesses.size() == processes.size();
    }

    /**
     * Updates the Gantt chart with the current process execution.
     * @param process The process being executed.
     * @param startTime The start time of execution.
     * @param endTime The end time of execution.
     */
    protected void updateGanttChart(Process process, int startTime, int endTime) {
        ganttChart.add(String.format("Time %d-%d: %s", startTime, endTime, process.getName()));
    }

    /**
     * Sorts processes by their arrival time.
     */
    protected void sortByArrivalTime() {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
    }

    /**
     * Sorts processes by their burst time.
     */
    protected void sortByBurstTime() {
        processes.sort(Comparator.comparingInt(Process::getBurstTime));
    }

    /**
     * Sorts processes by their priority.
     */
    protected void sortByPriority() {
        processes.sort(Comparator.comparingInt(Process::getPriority));
    }
}
