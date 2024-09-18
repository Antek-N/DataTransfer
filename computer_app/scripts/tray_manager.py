from PyQt5.QtWidgets import QApplication, QSystemTrayIcon, QMenu, QAction
from PyQt5.QtCore import Qt, QRect, QPropertyAnimation
from PyQt5.QtGui import QIcon
import qdarkstyle
from scripts.window_creator import WindowCreator
from scripts.app_config import AppConfig


class TrayManager(QApplication):
    """
    Manages a system tray icon and slide animations for window positioning.
    """
    def __init__(self, args: list):
        super().__init__(args)
        # Create Qt window
        self.window = WindowCreator()

        self.animation = None

        # Set program style
        self.setStyleSheet(qdarkstyle.load_stylesheet_pyqt5())

        # Set window flags and attributes
        self.window.setWindowFlags(Qt.Tool | Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint)
        self.window.setAttribute(Qt.WA_TranslucentBackground)

        # Set tray icon and tray icon menu
        self.tray_icon = QSystemTrayIcon(QIcon(AppConfig.ICON_PATH), self)
        tray_menu = QMenu()
        exit_action = QAction("Exit", self)
        exit_action.triggered.connect(lambda: self.quit())
        tray_menu.addAction(exit_action)
        self.tray_icon.setContextMenu(tray_menu)
        self.tray_icon.show()

        # Connect tray icon click event
        self.tray_icon.activated.connect(self.handle_tray_icon_click)

    def handle_tray_icon_click(self, reason: QSystemTrayIcon.ActivationReason) -> None:
        """
        Handles tray icon click event to toggle window visibility.

        :param reason: the reason for the tray icon activation
        :return: None
        """
        # Perform an action if the icon was pressed
        if reason == QSystemTrayIcon.Trigger:
            self.toggle_window()

    def toggle_window(self) -> None:
        """
        Toggles the visibility of the notification window, showing or hiding it.

        :param: None
        :return: None
        """
        if self.window.isVisible():
            self.animate_window_out()
        else:
            self.position_window_above_tray()
            self.animate_window_in()

    def animate_window_in(self) -> None:
        """
        Animates the window sliding in from the bottom of the screen.

        :param: None
        :return: None
        """
        # The starting position is below the screen. End position was set by position_window_above_tray method
        start_pos = QRect(
            self.window.x(),
            QApplication.primaryScreen().geometry().height(),
            self.window.width(),
            self.window.height()
        )
        end_pos = self.window.geometry()

        # Set an animation
        self.animation = QPropertyAnimation(self.window, b"geometry")
        self.animation.setDuration(300)
        self.animation.setStartValue(start_pos)
        self.animation.setEndValue(end_pos)
        self.window.show()
        self.animation.start()

    def animate_window_out(self) -> None:
        """
        Animates the window sliding out to the bottom of the screen.

        :param: None
        :return: None
        """
        # The starting position was set by position_window_above_tray method. End position is below the screen
        start_pos = self.window.geometry()
        end_pos = QRect(
            self.window.x(),
            QApplication.primaryScreen().geometry().height(),
            self.window.width(),
            self.window.height()
        )

        # Set an animation
        self.animation = QPropertyAnimation(self.window, b"geometry")
        self.animation.setDuration(300)
        self.animation.setStartValue(start_pos)
        self.animation.setEndValue(end_pos)
        self.animation.finished.connect(self.window.hide)  # Hide window after animation
        self.animation.start()

    def position_window_above_tray(self) -> None:
        """
        Positions the notification window directly above the system tray icon.

        :param: None
        :return: None
        """
        # Calculate window position based on tray icon and screen geometry
        screen_geometry = QApplication.primaryScreen().geometry()
        tray_geometry = self.tray_icon.geometry()
        x = tray_geometry.x() + tray_geometry.width() / 2 - self.window.width() / 2
        y = screen_geometry.height() - self.window.height() - tray_geometry.height()

        # Check if the window doesn't extend beyond the right edge
        if x + self.window.width() > screen_geometry.width():
            x = screen_geometry.width() - self.window.width()

        # Set window position
        self.window.setGeometry(int(x), int(y), self.window.width(), self.window.height())
