import asyncio
import websockets
import subprocess
from prediction import load_models, predict_new_audio, predict_new_gesture, combine_predict
import logging

# set up logging
logging.basicConfig(
    format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)

# load models
audio_model, gesture_model = load_models()
logging.info("audio_model, gesture_model are loaded")

output_audio_path = "out/Audio/output"
output_gesture_path = "out/Gesture/output.csv"


async def handle_message(websocket, path):
    while True:
        message = await websocket.recv()
        # Decode the received message as a byte array
        byte_array = bytearray(message)

        # check the 1st to 4th element, if the input is not a csv file (with content 'Time')
        if (byte_array[0:4] != b'Time'):
            # 1. save the raw audio file as 'out/Audio/output.raw'
            filename = f"{output_audio_path}.raw"
            with open(filename, "wb") as f:
                f.write(message)
            # 2. convert raw audio file to mp3 using convertRawAudio()
            convertRawAudio(filePath=filename,
                            outputPath=f'{output_audio_path}.mp3')
            logging.info("audio data recieved, saved and coverted")
        else:
            # save the csv file to folder 'out/Gesture/output.csv'
            filename = output_gesture_path
            with open(filename, "wb") as f:
                f.write(message)
            logging.info("gesture data recieved,saved")

            """
            csv file is always sent later than audio file, so prediction will only be carried out once the csv file is in. 
            """
            # Response will be the class ID of the predicted subject. I have hardcoded here. However, you can replace the response variable with your model output accordingly.
            audio_result = predict_new_audio(
                f"{output_audio_path}.mp3", audio_model)
            gesture_result = predict_new_gesture(
                output_gesture_path, gesture_model)
            combine_prediction = combine_predict(audio_result, gesture_result)
            response = str(combine_prediction)

            # Encode the string as bytes and send it back to the client
            await websocket.send(response.encode())


def convertRawAudio(filePath, outputPath='out/Audio/output.mp3'):
    cmd = ['ffmpeg', '-f', 's16le', '-ar', '44100',
           '-ac', '1', '-i', filePath, outputPath]
    subprocess.run(cmd)
    logging.info("file conversion completed")


async def main():
    # Start the WebSocket server on port 8086
    async with websockets.serve(handle_message, "localhost", 8086):
        await asyncio.Future()  # Run forever

asyncio.run(main())
