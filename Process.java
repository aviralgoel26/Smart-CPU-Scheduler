package com.cpuscheduler.model;

/**
 * Represents a process in the CPU scheduling simulation.
 */
public class Process {
    private final int id;
    private final String name;
    private final int arrivalTime;
    private final int burstTime;
    private final int priority; // Lower number indicates higher priority
    private int remainingTime;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int responseTime;
    private boolean isStarted;

    public Process(int id, String name, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.completionTime = -1;
        this.turnaroundTime = -1;
        this.waitingTime = -1;
        this.responseTime = -1;
        this.isStarted = false;
    }

    /**
     * Copy constructor to create a copy of an existing process.
     * @param other The process to copy.
     */
    public Process(Process other) {
        this.id = other.id;
        this.name = other.name;
        this.arrivalTime = other.arrivalTime;
        this.burstTime = other.burstTime;
        this.priority = other.priority;
        this.remainingTime = other.remainingTime;
        this.completionTime = other.completionTime;
        this.turnaroundTime = other.turnaroundTime;
        this.waitingTime = other.waitingTime;
        this.responseTime = other.responseTime;
        this.isStarted = other.isStarted;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public boolean isCompleted() {
        return remainingTime <= 0;
    }

    // Setters
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
        this.turnaroundTime = this.completionTime - this.arrivalTime;
        this.waitingTime = this.turnaroundTime - this.burstTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * Executes the process for a given time quantum.
     * @param timeQuantum The time quantum to execute the process for.
     * @param currentTime The current time in the scheduler.
     * @return The actual time the process was executed for.
     */
    public int execute(int timeQuantum, int currentTime) {
        if (!isStarted) {
            setResponseTime(currentTime - arrivalTime);
            isStarted = true;
        }

        int executionTime = Math.min(timeQuantum, remainingTime);
        remainingTime -= executionTime;
        
        if (isCompleted()) {
            setCompletionTime(currentTime + executionTime);
        }
        
        return executionTime;
    }

    @Override
    public String toString() {
        return String.format("Process{id=%d, name='%s', arrival=%d, burst=%d, priority=%d}",
                id, name, arrivalTime, burstTime, priority);
    }
}
