class Process:
    def __init__(self, pid, arrival_time, burst_time, priority=0):
        self.pid = pid
        self.arrival_time = arrival_time
        self.burst_time = burst_time
        self.priority = priority
        self.remaining_time = burst_time
        self.start_time = None
        self.completion_time = None
        
    @property
    def waiting_time(self):
        if self.start_time is None:
            return 0
        return self.start_time - self.arrival_time
    
    @property
    def turnaround_time(self):
        if self.completion_time is None:
            return 0
        return self.completion_time - self.arrival_time
