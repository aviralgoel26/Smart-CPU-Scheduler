from ui.main_window import MainWindow
import tkinter as tk

def main():
    root = tk.Tk()
    root.title("CPU Scheduler Simulator")
    app = MainWindow(root)
    root.mainloop()

if __name__ == "__main__":
    main()
