import tkinter as tk
from tkinter import ttk, messagebox
from visualization.gantt_chart import GanttChart
from core.process import Process
from core.fcfs_scheduler import FCFSScheduler
from core.sjf_scheduler import SJFScheduler
from core.rr_scheduler import RoundRobinScheduler
from .process_dialog import ProcessDialog

class MainWindow:
    def __init__(self, master):
        self.master = master
        self.master.geometry("1000x800")
        self.master.title("CPU Scheduler Simulator")
        self.processes = []
        
        # Create main containers with better padding and styling
        self.create_styles()
        self.create_menu()
        self.create_frames()
        self.setup_all_frames()
    
    def create_styles(self):
        style = ttk.Style()
        style.configure('Header.TLabel', font=('Helvetica', 12, 'bold'))
        style.configure('Info.TLabel', font=('Helvetica', 10))
        style.configure('Action.TButton', font=('Helvetica', 10))
    
    def create_menu(self):
        menubar = tk.Menu(self.master)
        self.master.config(menu=menubar)
        
        file_menu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="File", menu=file_menu)
        file_menu.add_command(label="Clear All", command=self._clear_simulation)
        file_menu.add_separator()
        file_menu.add_command(label="Exit", command=self.master.quit)
        
        help_menu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="Help", menu=help_menu)
        help_menu.add_command(label="About", command=self._show_about)
    
    def create_frames(self):
        # Main container with padding
        self.main_container = ttk.Frame(self.master, padding="10")
        self.main_container.pack(fill=tk.BOTH, expand=True)
        
        # Create frames with proper weights
        self.input_frame = ttk.LabelFrame(self.main_container, text="Scheduling Controls", padding="10")
        self.input_frame.pack(fill="x", padx=5, pady=5)
        
        self.process_frame = ttk.LabelFrame(self.main_container, text="Process Management", padding="10")
        self.process_frame.pack(fill="x", padx=5, pady=5)
        
        self.viz_frame = ttk.LabelFrame(self.main_container, text="Visualization", padding="10")
        self.viz_frame.pack(fill="both", expand=True, padx=5, pady=5)
        
        self.metrics_frame = ttk.LabelFrame(self.main_container, text="Performance Metrics", padding="10")
        self.metrics_frame.pack(fill="x", padx=5, pady=5)
    
    def setup_all_frames(self):
        self._setup_input_controls()
        self._setup_process_list()
        self._setup_visualization()
        self._setup_metrics_display()
    
    def _setup_input_controls(self):
        controls_frame = ttk.Frame(self.input_frame)
        controls_frame.pack(fill="x", expand=True)
        
        # Algorithm selection with label
        algo_frame = ttk.Frame(controls_frame)
        algo_frame.pack(side="left", padx=5)
        
        ttk.Label(algo_frame, text="Algorithm:", style='Info.TLabel').pack(side="left", padx=5)
        self.algorithm = ttk.Combobox(algo_frame, values=["FCFS", "SJF", "Round Robin"], width=20)
        self.algorithm.set("FCFS")
        self.algorithm.pack(side="left", padx=5)
        
        # Time quantum input for Round Robin
        self.quantum_frame = ttk.Frame(algo_frame)
        self.quantum_frame.pack(side="left", padx=5)
        ttk.Label(self.quantum_frame, text="Time Quantum:", style='Info.TLabel').pack(side="left", padx=5)
        self.time_quantum = ttk.Spinbox(self.quantum_frame, from_=1, to=100, width=5)
        self.time_quantum.set("2")
        self.time_quantum.pack(side="left", padx=5)
        self.quantum_frame.pack_forget()  # Hide initially
        
        # Show/hide quantum input based on algorithm selection
        self.algorithm.bind('<<ComboboxSelected>>', self._on_algorithm_change)
        
        # Buttons frame
        button_frame = ttk.Frame(controls_frame)
        button_frame.pack(side="left", padx=20)
        
        ttk.Button(button_frame, text="Add Process", style='Action.TButton',
                  command=self._show_process_dialog).pack(side="left", padx=5)
        
        ttk.Button(button_frame, text="Run Simulation", style='Action.TButton',
                  command=self._run_simulation).pack(side="left", padx=5)
        
        ttk.Button(button_frame, text="Clear All", style='Action.TButton',
                  command=self._clear_simulation).pack(side="left", padx=5)
    
    def _setup_process_list(self):
        # Create frame for process list and buttons
        list_frame = ttk.Frame(self.process_frame)
        list_frame.pack(fill="both", expand=True)
        
        # Create treeview with scrollbar
        tree_frame = ttk.Frame(list_frame)
        tree_frame.pack(fill="both", expand=True)
        
        self.tree_scroll = ttk.Scrollbar(tree_frame)
        self.tree_scroll.pack(side="right", fill="y")
        
        columns = ('PID', 'Arrival Time', 'Burst Time', 'Priority')
        self.process_tree = ttk.Treeview(tree_frame, columns=columns, show='headings',
                                       yscrollcommand=self.tree_scroll.set)
        
        # Configure scrollbar
        self.tree_scroll.config(command=self.process_tree.yview)
        
        # Set column headings and widths
        for col in columns:
            self.process_tree.heading(col, text=col, command=lambda c=col: self._sort_processes(c))
            self.process_tree.column(col, width=100, anchor="center")
        
        self.process_tree.pack(fill="both", expand=True)
        
        # Add right-click menu
        self.tree_menu = tk.Menu(self.process_tree, tearoff=0)
        self.tree_menu.add_command(label="Edit", command=self._edit_selected_process)
        self.tree_menu.add_command(label="Delete", command=self._delete_selected_process)
        
        self.process_tree.bind("<Button-3>", self._show_tree_menu)
        self.process_tree.bind("<Double-1>", lambda e: self._edit_selected_process())
    
    def _sort_processes(self, column):
        """Sort processes by clicking on column headers"""
        items = [(self.process_tree.set(item, column), item) for item in self.process_tree.get_children('')]
        
        # Convert to appropriate type for sorting
        if column in ('Arrival Time', 'Burst Time'):
            items = [(float(value), item) for value, item in items]
        elif column == 'Priority':
            items = [(int(value) if value else 0, item) for value, item in items]
        
        # Sort items
        items.sort(reverse=self.process_tree.get('sort_reverse', False))
        
        # Rearrange items in sorted positions
        for index, (_, item) in enumerate(items):
            self.process_tree.move(item, '', index)
        
        # Reverse sort next time
        self.process_tree['sort_reverse'] = not self.process_tree.get('sort_reverse', False)
    
    def _show_tree_menu(self, event):
        """Show context menu on right click"""
        item = self.process_tree.identify_row(event.y)
        if item:
            self.process_tree.selection_set(item)
            self.tree_menu.post(event.x_root, event.y_root)
    
    def _edit_selected_process(self):
        """Edit the selected process"""
        selection = self.process_tree.selection()
        if not selection:
            return
        
        item = selection[0]
        values = self.process_tree.item(item)['values']
        
        # Find process object
        process = next((p for p in self.processes if p.pid == values[0]), None)
        if not process:
            return
        
        # Show edit dialog
        dialog = ProcessDialog(self.master, process)
        self.master.wait_window(dialog)
        
        if dialog.process:
            # Update process in list
            idx = self.processes.index(process)
            self.processes[idx] = dialog.process
            
            # Update treeview
            self.process_tree.item(item, values=(
                dialog.process.pid,
                dialog.process.arrival_time,
                dialog.process.burst_time,
                dialog.process.priority
            ))
    
    def _delete_selected_process(self):
        """Delete the selected process"""
        selection = self.process_tree.selection()
        if not selection:
            return
        
        if messagebox.askyesno("Confirm Delete", "Are you sure you want to delete this process?"):
            item = selection[0]
            values = self.process_tree.item(item)['values']
            
            # Remove from processes list
            self.processes = [p for p in self.processes if p.pid != values[0]]
            
            # Remove from treeview
            self.process_tree.delete(item)
    
    def _show_process_dialog(self):
        dialog = ProcessDialog(self.master)
        self.master.wait_window(dialog)
        
        if dialog.process:
            # Check for duplicate PID
            if any(p.pid == dialog.process.pid for p in self.processes):
                messagebox.showerror("Error", "Process ID already exists!")
                return
            
            self.processes.append(dialog.process)
            self.process_tree.insert('', 'end', values=(
                dialog.process.pid,
                dialog.process.arrival_time,
                dialog.process.burst_time,
                dialog.process.priority
            ))
    
    def _on_algorithm_change(self, event=None):
        if self.algorithm.get() == "Round Robin":
            self.quantum_frame.pack(side="left", padx=5)
        else:
            self.quantum_frame.pack_forget()
    
    def _run_simulation(self):
        if not self.processes:
            messagebox.showwarning("Warning", "Please add some processes first!")
            return
            
        algorithm = self.algorithm.get()
        if algorithm == "FCFS":
            scheduler = FCFSScheduler()
        elif algorithm == "SJF":
            scheduler = SJFScheduler()
        elif algorithm == "Round Robin":
            try:
                time_quantum = int(self.time_quantum.get())
                if time_quantum < 1:
                    raise ValueError("Time quantum must be positive")
                scheduler = RoundRobinScheduler(time_quantum)
            except ValueError as e:
                messagebox.showerror("Error", str(e))
                return
        else:
            messagebox.showerror("Error", "Invalid scheduling algorithm!")
            return
        
        # Run simulation
        for process in self.processes:
            scheduler.add_process(process)
        
        scheduler.schedule()
        
        # Update visualization
        self.gantt_chart.update(scheduler.execution_sequence)
        
        # Update metrics
        metrics = scheduler.get_metrics()
        self.avg_waiting_label.config(
            text=f"Average Waiting Time: {metrics['avg_waiting_time']:.2f}")
        self.avg_turnaround_label.config(
            text=f"Average Turnaround Time: {metrics['avg_turnaround_time']:.2f}")
    
    def _clear_simulation(self):
        if messagebox.askyesno("Confirm Clear", "Are you sure you want to clear all processes and reset the simulation?"):
            self.processes.clear()
            for item in self.process_tree.get_children():
                self.process_tree.delete(item)
            self.avg_waiting_label.config(text="Average Waiting Time: -")
            self.avg_turnaround_label.config(text="Average Turnaround Time: -")
            self.gantt_chart.update([])
    
    def _show_about(self):
        messagebox.showinfo("About CPU Scheduler Simulator",
                          "CPU Scheduler Simulator\n\n"
                          "A visual tool for demonstrating CPU scheduling algorithms.\n\n"
                          "Supported Algorithms:\n"
                          "- First Come First Serve (FCFS)\n"
                          "- Shortest Job First (SJF)\n"
                          "- Round Robin\n\n"
                          "Features:\n"
                          "- Real-time visualization\n"
                          "- Process management\n"
                          "- Performance metrics")
    
    def _setup_visualization(self):
        self.gantt_chart = GanttChart(self.viz_frame)
    
    def _setup_metrics_display(self):
        self.avg_waiting_label = ttk.Label(self.metrics_frame, 
                                         text="Average Waiting Time: -")
        self.avg_waiting_label.pack(side="left", padx=5)
        
        self.avg_turnaround_label = ttk.Label(self.metrics_frame,
                                            text="Average Turnaround Time: -")
        self.avg_turnaround_label.pack(side="left", padx=5)
