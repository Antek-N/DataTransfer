import sys
from scripts.tray_manager import TrayManager


if __name__ == "__main__":
    # Create an instance of TrayManager (a class responsible for managing the program window)
    app = TrayManager(sys.argv)
    # Start the application's event loop
    sys.exit(app.exec_())
