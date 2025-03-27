from abc import ABC, abstractmethod
from typing import List
from .process import Process

class Scheduler(ABC):
    def __init__(self):
        self.processes: List[Process] = []
        self.current_time = 0
        self.execution_sequence = []

    @abstractmethod
    def schedule(self) -> None:
        pass

    def add_process(self, process: Process) -> None:
        self.processes.append(process)

    def get_metrics(self):
        total_waiting_time = sum(p.waiting_time for p in self.processes)
        total_turnaround_time = sum(p.turnaround_time for p in self.processes)
        n = len(self.processes)
        
        return {
            'avg_waiting_time': total_waiting_time / n if n > 0 else 0,
            'avg_turnaround_time': total_turnaround_time / n if n > 0 else 0
        }
