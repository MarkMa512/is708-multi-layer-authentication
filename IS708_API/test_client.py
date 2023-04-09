import websocket
import logging

# set up logging
logging.basicConfig(
    format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)

# create a websocket connection to the server
ws = websocket.create_connection("ws://localhost:8086/")
logging.info("Connected to server.")

# read a raw audio file
with open('model_training/sample.raw', 'rb') as f:
    audio_file = f.read()

# read a gesture csv  file
with open('model_training/Gesture/3/1677689358090.csv', 'rb') as f:
    gesture_file = f.read()

# send the audio as a binary message to the server
ws.send_binary(audio_file)
logging.info(f"Sent {len(audio_file)} bytes of audio data to the server.")

# send the gesture asa binary message to the server
ws.send_binary(gesture_file)
logging.info(f"Sent {len(gesture_file)} bytes of gesture data to the server.")

# receive and print responses from the server
while True:
    try:
        response = ws.recv()
        logging.info(f"Received response: {response}")
        if response == b'3':
            # the expected response is b'3'
            logging.info("========== Server Setup Success! ==========")
    except websocket.WebSocketConnectionClosedException:
        logging.warning("WebSocket connection closed.")
        break

# close the websocket connection
ws.close()
logging.info("Disconnected from server.")