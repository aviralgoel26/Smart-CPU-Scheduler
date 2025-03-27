from .scheduler import Scheduler
from .process import Process
from typing import List

class FCFSScheduler(Scheduler):
    """First Come First Serve Scheduler Implementation"""
    
    def schedule(self) -> None:
        # Sort processes by arrival time
        self.processes.sort(key=lambda x: x.arrival_time)
        
        self.current_time = 0
        self.execution_sequence = []
        
        for process in self.processes:
            # If there's a gap between processes, move current time to process arrival
            if self.current_time < process.arrival_time:
                self.current_time = process.arrival_time
            
            # Set process start time
            process.start_time = self.current_time
            
            # Execute process
            start = self.current_time
            self.current_time += process.burst_time
            
            # Set completion time
            process.completion_time = self.current_time
            
            # Record execution sequence for visualization
            self.execution_sequence.append((process, start, self.current_time))
