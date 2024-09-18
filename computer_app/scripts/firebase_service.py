import json
import requests
from firebase_admin import credentials, initialize_app
from scripts.app_config import AppConfig


class FirebaseService:
    """
    Initializes Firebase and provides a method to send FCM messages to devices.
    """
    def __init__(self) -> None:
        self.project_id = AppConfig.get_project_id()
        # Initialize Firebase app with credentials
        credentials_obj = credentials.Certificate(AppConfig.FIREBASE_KEY_FILE_PATH)
        initialize_app(credentials_obj)

    def send_fcm_message(self, token: str, message_body: str) -> tuple[bool, str | dict]:
        """
        Sends an FCM message to a specific device.

        :param token: The android device token
        :param message_body: The body of the message to be sent
        :return: A tuple containing a boolean indicating success, and the response data (JSON or error text)
        """
        api_url: str = f"https://fcm.googleapis.com/v1/projects/{self.project_id}/messages:send"
        headers: dict = {
            "Authorization": "Bearer " + AppConfig.get_access_token(),
            "Content-Type": "application/json; UTF-8",
        }

        # Construct the message payload
        message: dict = {
            "message": {
                "token": token,
                "data": {
                    "title": "Press button to copy",
                    "body": message_body,
                    "copy": "true"
                },
                "apns": {
                    "payload": {
                        "aps": {
                            "sound": "default",
                        },
                    },
                },
                "android": {
                    "priority": "high",
                    "ttl": "4500s"
                }
            }
        }

        # Send the request with notification data to Firebase
        response = requests.post(api_url, headers=headers, data=json.dumps(message))

        # Check if the response was successful
        if response.status_code == 200:
            return True, response.json()
        else:
            return False, response.text
