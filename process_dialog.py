import tkinter as tk
from tkinter import ttk, messagebox
from core.process import Process

class ProcessDialog(tk.Toplevel):
    def __init__(self, parent, process=None):
        super().__init__(parent)
        self.title("Add Process" if process is None else "Edit Process")
        self.process = None
        self.editing_process = process
        
        # Make dialog modal
        self.transient(parent)
        self.grab_set()
        
        # Create form
        self._create_widgets()
        
        # If editing, populate fields
        if process:
            self.pid_var.set(process.pid)
            self.arrival_var.set(str(process.arrival_time))
            self.burst_var.set(str(process.burst_time))
            self.priority_var.set(str(process.priority))
            self.pid_entry.config(state='disabled')  # Don't allow PID editing
        
        # Center dialog
        self.geometry("300x250")
        self.resizable(False, False)
        
        # Set focus
        if process:
            self.arrival_entry.focus_set()
        else:
            self.pid_entry.focus_set()
    
    def _create_widgets(self):
        # Main frame with padding
        main_frame = ttk.Frame(self, padding="10")
        main_frame.pack(fill="both", expand=True)
        
        # Process ID
        pid_frame = ttk.Frame(main_frame)
        pid_frame.pack(fill="x", pady=5)
        ttk.Label(pid_frame, text="Process ID:").pack(side="left")
        self.pid_var = tk.StringVar()
        self.pid_entry = ttk.Entry(pid_frame, textvariable=self.pid_var)
        self.pid_entry.pack(side="right", expand=True, fill="x", padx=(10, 0))
        
        # Arrival Time
        arrival_frame = ttk.Frame(main_frame)
        arrival_frame.pack(fill="x", pady=5)
        ttk.Label(arrival_frame, text="Arrival Time:").pack(side="left")
        self.arrival_var = tk.StringVar()
        self.arrival_entry = ttk.Entry(arrival_frame, textvariable=self.arrival_var)
        self.arrival_entry.pack(side="right", expand=True, fill="x", padx=(10, 0))
        
        # Burst Time
        burst_frame = ttk.Frame(main_frame)
        burst_frame.pack(fill="x", pady=5)
        ttk.Label(burst_frame, text="Burst Time:").pack(side="left")
        self.burst_var = tk.StringVar()
        self.burst_entry = ttk.Entry(burst_frame, textvariable=self.burst_var)
        self.burst_entry.pack(side="right", expand=True, fill="x", padx=(10, 0))
        
        # Priority
        priority_frame = ttk.Frame(main_frame)
        priority_frame.pack(fill="x", pady=5)
        ttk.Label(priority_frame, text="Priority:").pack(side="left")
        self.priority_var = tk.StringVar()
        self.priority_entry = ttk.Entry(priority_frame, textvariable=self.priority_var)
        self.priority_entry.pack(side="right", expand=True, fill="x", padx=(10, 0))
        
        # Help text
        help_text = "Note: Lower priority number = Higher priority"
        ttk.Label(main_frame, text=help_text, foreground="gray").pack(pady=5)
        
        # Buttons
        button_frame = ttk.Frame(main_frame)
        button_frame.pack(fill="x", pady=20)
        
        ttk.Button(button_frame, text="OK", command=self._on_ok).pack(side=tk.LEFT, padx=5)
        ttk.Button(button_frame, text="Cancel", command=self._on_cancel).pack(side=tk.LEFT, padx=5)
        
        # Bind Enter key to OK button
        self.bind("<Return>", lambda e: self._on_ok())
        self.bind("<Escape>", lambda e: self._on_cancel())
    
    def _validate_inputs(self):
        try:
            pid = self.pid_var.get().strip()
            if not pid:
                raise ValueError("Process ID is required")
            
            arrival_time = float(self.arrival_var.get())
            if arrival_time < 0:
                raise ValueError("Arrival time cannot be negative")
            
            burst_time = float(self.burst_var.get())
            if burst_time <= 0:
                raise ValueError("Burst time must be positive")
            
            priority = int(self.priority_var.get() or "0")
            
            return True
        except ValueError as e:
            messagebox.showerror("Invalid Input", str(e))
            return False
    
    def _on_ok(self):
        if not self._validate_inputs():
            return
        
        self.process = Process(
            pid=self.pid_var.get().strip(),
            arrival_time=float(self.arrival_var.get()),
            burst_time=float(self.burst_var.get()),
            priority=int(self.priority_var.get() or "0")
        )
        self.destroy()
    
    def _on_cancel(self):
        self.process = None
        self.destroy()
