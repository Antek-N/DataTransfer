import os
from PyQt5.QtWidgets import (
    QMainWindow, QLabel, QLineEdit, QTextEdit, QPushButton, QVBoxLayout, QWidget, QMessageBox, QCheckBox)
from PyQt5.QtCore import QTimer
from PyQt5.QtGui import QFont
from scripts.app_config import AppConfig
from scripts.firebase_service import FirebaseService


class WindowCreator(QMainWindow):
    """
    Creates and handles program window.
    """
    def __init__(self) -> None:
        super().__init__()
        self.send_button = None
        self.remember_token_checkbox = None
        self.body_input = None
        self.body_label = None
        self.token_input = None
        self.token_label = None

        self.firebase_service = FirebaseService()
        self.initialize_ui()

    def initialize_ui(self) -> None:
        """
        Initializes the GUI components and layout.

        :param: None
        :return: None
        """
        # Initialize the window and fonts
        self.setWindowTitle('FCM Notification Sender')
        self.setFixedSize(400, 270)
        regular_font = QFont("Segoe UI", 10)
        bold_font = QFont("Segoe UI", 10, QFont.Bold)

        # Initialize labels and input fields
        self.token_label = QLabel('Device Token:', self)
        self.token_label.setFont(bold_font)
        self.token_input = QLineEdit(self)
        self.token_input.setFont(regular_font)

        self.body_label = QLabel('Notification Body:', self)
        self.body_label.setFont(bold_font)
        self.body_input = QTextEdit(self)
        self.body_input.setFont(regular_font)

        self.remember_token_checkbox = QCheckBox("Remember Token", self)
        self.remember_token_checkbox.setFont(regular_font)

        self.send_button = QPushButton('Send Notification', self)
        self.send_button.setFont(bold_font)
        self.send_button.clicked.connect(self.handle_send_button)

        # Load the saved device token from a file if it exists
        self.load_saved_token()

        # Set layout for the window
        layout = QVBoxLayout()
        layout.addWidget(self.token_label)
        layout.addWidget(self.token_input)
        layout.addWidget(self.body_label)
        layout.addWidget(self.body_input)
        layout.addWidget(self.remember_token_checkbox)
        layout.addWidget(self.send_button)

        container = QWidget()
        container.setLayout(layout)
        self.setCentralWidget(container)

        # Style for the UI components
        self.setStyleSheet("""
            QMainWindow {
                border-radius: 15px;
                background-color: #2C2C2C;
                color: #FFFFFF;
                border: 1px solid #4A4A4A;
            }
            QWidget {
                background-color: #2C2C2C;
                color: #FFFFFF;
                border-radius: 15px;
            }
            QLabel {
                font-size: 12px;
            }
            QLineEdit, QTextEdit {
                background-color: #393939;
                border: 1px solid #4A4A4A;
                border-radius: 5px;
                padding: 5px;
                color: #FFFFFF;
            }
            QCheckBox {
                padding: 5px;
            }
            QPushButton {
                background-color: #5A5A5A;
                border: none;
                border-radius: 10px;
                padding: 8px;
                color: white;
            }
            QPushButton:hover {
                background-color: #7A7A7A;
            }
            QPushButton:pressed {
                background-color: #4A4A4A;
            }
        """)

    def handle_send_button(self) -> None | int:
        """
        Handles the action of sending a notification when the send button is clicked.

        :param: None
        :return: 1 if there is an input error, otherwise None
        """
        # Get token and body from fields
        token: str = self.token_input.text().strip()
        body: str = self.body_input.toPlainText().strip()

        # Check if token or body is empty
        if not token or not body:
            QMessageBox.warning(self, "Input Error", "All fields must be filled out.")
            return 1

        # Save or delete token based on checkbox state
        if self.remember_token_checkbox.isChecked():
            self.save_token()
        else:
            self.delete_saved_token()

        # Send FCM message using the firebase service
        success, response = self.firebase_service.send_fcm_message(token, body)

        # If success change the button color to green (otherwise to red) for 2 seconds
        if success:
            self.send_button.setStyleSheet("background-color: #28A745;")
            QTimer.singleShot(2000, lambda: self.send_button.setStyleSheet(""))
        else:
            self.send_button.setStyleSheet("background-color: #DC3545;")
            QTimer.singleShot(2000, lambda: self.send_button.setStyleSheet(""))
            QMessageBox.critical(self, "Error", "Failed to send notification.\n" + response)

    def load_saved_token(self) -> None:
        """
        Loads the saved device token from a file if it exists.

        :param: None
        :return: None
        """
        token_file_path = f"{AppConfig.TOKEN_FOLDER_PATH}/saved_token.txt"

        if os.path.exists(token_file_path):
            with open(token_file_path, "r") as file:
                saved_token = file.read().strip()
                self.token_input.setText(saved_token)
                self.remember_token_checkbox.setChecked(True)

    def save_token(self) -> None:
        """
        Saves the device token to a file.

        :param: None
        :return: None
        """
        token = self.token_input.text().strip()
        token_file_path = f"{AppConfig.TOKEN_FOLDER_PATH}/saved_token.txt"

        # Create the folder storing the file if it doesn't exist
        if not os.path.exists(AppConfig.TOKEN_FOLDER_PATH):
            os.makedirs(AppConfig.TOKEN_FOLDER_PATH)

        # Save token to file
        with open(token_file_path, "w") as file:
            file.write(token)

    @staticmethod
    def delete_saved_token() -> None:
        """
        Deletes the saved device token file if it exists.

        :param: None
        :return: None
        """
        token_file_path = f"{AppConfig.TOKEN_FOLDER_PATH}/saved_token.txt"

        if os.path.exists(token_file_path):
            os.remove(token_file_path)
