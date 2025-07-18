package com.cpuscheduler.core;

/**
 * Factory class for creating different types of CPU schedulers.
 */
public class SchedulerFactory {
    public enum SchedulerType {
        FCFS,
        SJF,
        PRIORITY,
        ROUND_ROBIN
    }

    /**
     * Creates a scheduler of the specified type.
     * @param type The type of scheduler to create.
     * @param timeQuantum The time quantum for Round Robin scheduler (ignored for other types).
     * @return An instance of the specified scheduler.
     */
    public static Scheduler createScheduler(SchedulerType type, int timeQuantum) {
        switch (type) {
            case FCFS:
                return new FCFSScheduler();
            case SJF:
                return new SJFScheduler();
            case PRIORITY:
                return new PriorityScheduler();
            case ROUND_ROBIN:
                return new RoundRobinScheduler(timeQuantum);
            default:
                throw new IllegalArgumentException("Unknown scheduler type: " + type);
        }
    }

    /**
     * Creates a scheduler of the specified type with a default time quantum of 4 for Round Robin.
     * @param type The type of scheduler to create.
     * @return An instance of the specified scheduler.
     */
    public static Scheduler createScheduler(SchedulerType type) {
        return createScheduler(type, 4); // Default time quantum of 4 for Round Robin
    }
}
