import asyncio
import websockets
import subprocess
import joblib
import pandas as pd
import numpy as np
import librosa
from tensorflow.keras.preprocessing.sequence import pad_sequences

def load_models()->object:
    # Load the trained model
    audio_model = joblib.load('audio_svm_model.pkl')
    gesture_model = joblib.load('gesture_random_forest_classifier_model_relative_time.pkl')
    return audio_model, gesture_model

def predict_new_gesture(csv_file_path:str, clf:object)->int:
    df = pd.read_csv(csv_file_path)
    # Calculate the relative time difference for each row of reading 
    # compared to the 1st row of reading in each CSV file / reading 
    df['relative_time'] = df['Timestamp'].apply(lambda x: (x - df['Timestamp'][0]))
    df = df.drop(["Timestamp"], axis=1)  # Drop "Timestamp" columns as features
    result_list = clf.predict(df)
    counts = np.bincount(result_list)
    result = np.argmax(counts)
    return result

def predict_new_audio(new_audio_path:str, svm:object, n_mfcc=20)->int:
    input_list = []
    signal_new, sr_new = librosa.load(new_audio_path, duration=3.0)
    mfcc_new = librosa.feature.mfcc(y=signal_new, sr=sr_new, n_mfcc=n_mfcc)
    mfcc_flattened=mfcc_new.T.flatten().tolist()
    input_list.append(mfcc_flattened)
    input_list = pad_sequences(input_list, maxlen=1500, dtype='float32', padding='post', truncating='post')
    y_pred = svm.predict(input_list)
    return y_pred


async def handle_message(websocket, path):
    while True:
        message = await websocket.recv()
        # Decode the received message as a byte array
        byte_array = bytearray(message)

        # Process the received input here.

        # Response will be the class ID of the predicted subject. I have hardcoded here. However, you can replace the response variable with your model output accordingly.
        response = "1"
        # Encode the string as bytes and send it back to the client
        await websocket.send(response.encode())


def convertRawAudio(filePath, outputPath='output.mp3'):
    cmd = ['ffmpeg', '-f', 's16le', '-ar', '44100',
           '-ac', '1', '-i', filePath, outputPath]
    subprocess.run(cmd)


async def main():
    # Start the WebSocket server on port 8765
    convertRawAudio('data/sample.raw')
    async with websockets.serve(handle_message, "localhost", 8086):
        await asyncio.Future()  # Run forever

asyncio.run(main())
