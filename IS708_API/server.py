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

# check if output folder exists, if not, create one
subprocess.run(['mkdir', '-p', 'out/Audio'])
subprocess.run(['mkdir', '-p', 'out/Gesture'])

# define output directories for the audio and csv file recieved from the client
output_audio_path = "out/Audio/output"
output_gesture_path = "out/Gesture/output.csv"


async def handle_message(websocket, path):
    while True:
        message = await websocket.recv()
        # Decode the received message as a byte array
        byte_array = bytearray(message)
        logging.info("Message recieved, byte array of length: " +
                     byte_array.__len__().__str__())

        # check the 1st to 4th element, if the input is not a csv file (with content 'Time')
        if (byte_array[0:4] != b'Time'):
            # 0. check if the audio file is already in the folder
            try:
                with open(f"{output_audio_path}.mp3", "rb") as f:
                    # remove the audio file if it already exists
                    subprocess.run(['rm', f'{output_audio_path}.mp3'])
                    subprocess.run(['rm', f'{output_audio_path}.raw'])
                    logging.info("Audio file already exists, deleted.")
            except FileNotFoundError:
                pass
            # 1. save the raw audio file as 'out/Audio/output.raw'
            filename = f"{output_audio_path}.raw"
            with open(filename, "wb") as f:
                f.write(message)
            # 2. convert raw audio file to mp3 using convertRawAudio()
            convertRawAudio(filePath=filename,
                            outputPath=f'{output_audio_path}.mp3')
            logging.info("Audio data recieved, saved and coverted")
        else:
            # 0. check if the audio file is already in the folder
            # if not, prompt the user to record audio first.
            try:
                with open(f"{output_audio_path}.mp3", "rb") as f:
                    pass
            except FileNotFoundError:
                await websocket.send("Please record audio first.")
                logging.info(
                    "Gesture CSV recieved first, but Audio file not found.")
                continue
            # 1. save the csv file to folder 'out/Gesture/output.csv'
            filename = output_gesture_path
            with open(filename, "wb") as f:
                f.write(message)
            logging.info("Gesture data recieved,saved")
            """
            csv file is always sent later than audio file, so prediction will only be carried out once the csv file is in. 
            """
            # 2. predict based on audio data
            audio_result = predict_new_audio(
                f"{output_audio_path}.mp3", audio_model)
            # 3. predict based on gesture data
            gesture_result = predict_new_gesture(
                output_gesture_path, gesture_model)
            # 4. combine result from both predictions
            combine_prediction = combine_predict(audio_result, gesture_result)
            # 5. encode the string as bytes and send it back to the client
            response = str(combine_prediction)
            await websocket.send(response)
            logging.info(
                f'Prediction result of :"{response}" has been sent to the client.')
            # 6. delete the audio and csv file
            subprocess.run(['rm', f'{output_audio_path}.mp3'])
            subprocess.run(['rm', f'{output_audio_path}.raw'])
            subprocess.run(['rm', f'{output_gesture_path}'])
            logging.info("Audio and gesture data deleted")


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
