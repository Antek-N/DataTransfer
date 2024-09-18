import sys
import os
import json
from google.oauth2 import service_account
from google.auth.transport.requests import Request


class AppConfig:
    """
    Provides methods to retrieve Google Cloud project ID and access token,
    and stores file paths.
    """
    # Set path to the firebase key file
    # Check if running in a PyInstaller exe
    if hasattr(sys, '_MEIPASS'):
        FIREBASE_KEY_FILE_PATH = os.path.join(sys._MEIPASS, 'firebase_key.json')  # Path to the firebase key file when running as exe
    else:
        FIREBASE_KEY_FILE_PATH = os.path.abspath("data/firebase_key.json")  # Path to the firebase key file when running from source code

    # Set path to the icon file
    # Check if running in a PyInstaller exe
    if hasattr(sys, '_MEIPASS'):
        ICON_PATH = os.path.join(sys._MEIPASS, 'icon.ico')  # Path to the icon file when running as exe
    else:
        ICON_PATH = os.path.abspath("icon/icon.ico")  # Path to the icon file when running from source code

    # Set path to the file storing the device token
    TOKEN_FILE_PATH = "data/saved_token.txt"

    @staticmethod
    def get_project_id() -> str:
        """
        Retrieves the Google Cloud project ID from firebase_key.json.

        :param: None
        :return: The project ID as a string.
        :raises FileNotFoundError: If the JSON file does not exist.
        """
        if os.path.exists(AppConfig.FIREBASE_KEY_FILE_PATH):
            # Load the JSON file and retrieve the project ID
            with open(AppConfig.FIREBASE_KEY_FILE_PATH, "r") as file:
                json_data = json.load(file)
                return json_data.get("project_id")
        else:
            # Raise an exception if the file is not found
            raise FileNotFoundError(f"json file not found")

    @staticmethod
    def get_access_token() -> str:
        """
        Retrieves an access token for Google Cloud services using a service account key.

        :param: None
        :return: The access token as a string.
        """
        # Load credentials from the json file
        credentials = service_account.Credentials.from_service_account_file(
            AppConfig.FIREBASE_KEY_FILE_PATH,
            scopes=["https://www.googleapis.com/auth/cloud-platform"],
        )
        # Refresh the credentials to get a valid token
        credentials.refresh(Request())
        return credentials.token
