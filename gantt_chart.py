import matplotlib.pyplot as plt
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import tkinter as tk

class GanttChart:
    def __init__(self, master):
        self.figure = Figure(figsize=(8, 2), dpi=100)
        self.ax = self.figure.add_subplot(111)
        self.canvas = FigureCanvasTkAgg(self.figure, master=master)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

    def update(self, execution_sequence):
        self.ax.clear()
        
        # Create Gantt chart
        for i, (process, start, end) in enumerate(execution_sequence):
            self.ax.barh(0, end - start, left=start, height=0.3,
                        label=f'P{process.pid}')
            self.ax.text(start + (end - start)/2, 0,
                        f'P{process.pid}',
                        ha='center', va='center')

        self.ax.set_ylabel('CPU')
        self.ax.set_xlabel('Time')
        self.ax.grid(True)
        
        self.canvas.draw()
