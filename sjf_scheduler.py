from .scheduler import Scheduler
from .process import Process
from typing import List
import copy

class SJFScheduler(Scheduler):
    """Shortest Job First Scheduler Implementation"""
    
    def schedule(self) -> None:
        # Create a copy of processes to avoid modifying original arrival times
        remaining_processes = [copy.deepcopy(p) for p in self.processes]
        ready_queue = []
        self.current_time = 0
        self.execution_sequence = []
        
        # Sort initially by arrival time
        remaining_processes.sort(key=lambda x: x.arrival_time)
        
        while remaining_processes or ready_queue:
            # Add all processes that have arrived to ready queue
            while remaining_processes and remaining_processes[0].arrival_time <= self.current_time:
                ready_queue.append(remaining_processes.pop(0))
            
            if not ready_queue:
                # No process available, jump to next arrival time
                self.current_time = remaining_processes[0].arrival_time
                continue
            
            # Sort ready queue by burst time (shortest job first)
            ready_queue.sort(key=lambda x: x.burst_time)
            
            # Get shortest job
            current_process = ready_queue.pop(0)
            
            # Find original process to update its times
            original_process = next(p for p in self.processes 
                                 if p.pid == current_process.pid)
            
            # Set start time if not already set
            if original_process.start_time is None:
                original_process.start_time = self.current_time
            
            # Execute process
            start = self.current_time
            self.current_time += current_process.burst_time
            
            # Update completion time
            original_process.completion_time = self.current_time
            
            # Add to execution sequence
            self.execution_sequence.append((original_process, start, self.current_time))
