from collections import deque
from .scheduler import Scheduler
from .process import Process

class RoundRobinScheduler(Scheduler):
    def __init__(self, time_quantum=2):
        super().__init__()
        self.time_quantum = time_quantum
        
    def schedule(self) -> None:
        # Reset all processes
        for process in self.processes:
            process.remaining_time = process.burst_time
            process.start_time = None
            process.completion_time = None
        
        # Sort processes by arrival time
        self.processes.sort(key=lambda p: p.arrival_time)
        
        ready_queue = deque()
        current_time = 0
        remaining_processes = len(self.processes)
        process_index = 0
        
        while remaining_processes > 0:
            # Add newly arrived processes to ready queue
            while process_index < len(self.processes) and self.processes[process_index].arrival_time <= current_time:
                ready_queue.append(self.processes[process_index])
                process_index += 1
            
            if not ready_queue:
                # No process available, advance time to next arrival
                if process_index < len(self.processes):
                    current_time = self.processes[process_index].arrival_time
                    continue
                break
            
            # Get next process from ready queue
            current_process = ready_queue.popleft()
            
            # Set start time if not set
            if current_process.start_time is None:
                current_process.start_time = current_time
            
            # Calculate execution time for this quantum
            execution_time = min(self.time_quantum, current_process.remaining_time)
            
            # Update current process
            current_process.remaining_time -= execution_time
            current_time += execution_time
            
            # Record execution for Gantt chart
            self.execution_sequence.append((current_process, current_time - execution_time, current_time))
            
            # Process completed
            if current_process.remaining_time == 0:
                current_process.completion_time = current_time
                remaining_processes -= 1
            else:
                # Add back to ready queue if not completed
                # But first add any processes that arrived during this execution
                while process_index < len(self.processes) and self.processes[process_index].arrival_time <= current_time:
                    ready_queue.append(self.processes[process_index])
                    process_index += 1
                ready_queue.append(current_process)
