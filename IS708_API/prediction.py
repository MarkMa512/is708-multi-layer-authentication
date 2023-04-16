import pandas as pd
import numpy as np
import librosa
import joblib
# from tensorflow.keras.preprocessing.sequence import pad_sequences
import logging

# set up logging
logging.basicConfig(
    format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)


def load_models() -> object:
    # Load the trained model
    logging.info("loading audio and gesture models... Please wait.")
    audio_model = joblib.load('model_training/audio_svm_model.pkl')
    gesture_model = joblib.load(
        'model_training/gesture_random_forest_classifier_model_relative_time.pkl')
    logging.info("audio and gesture models are loaded!")
    return audio_model, gesture_model


def predict_new_gesture(csv_file_path: str, clf: object) -> object:
    df = pd.read_csv(csv_file_path)
    # Calculate the relative time difference for each row of reading
    # compared to the 1st row of reading in each CSV file / reading
    df['relative_time'] = df['Timestamp'].apply(
        lambda x: (x - df['Timestamp'][0]))
    df = df.drop(["Timestamp"], axis=1)  # Drop "Timestamp" columns as features
    result_list = clf.predict(df)
    logging.info("gesture prediction result: " + str(result_list[:4]))
    return result_list


def predict_new_audio(new_audio_path: str, svm: object, n_mfcc=20) -> int:
    input_list = []
    signal_new, sr_new = librosa.load(new_audio_path, duration=3.0)
    mfcc_new = librosa.feature.mfcc(y=signal_new, sr=sr_new, n_mfcc=n_mfcc)
    mfcc_flattened = mfcc_new.T.flatten().tolist()
    input_list.append(mfcc_flattened)

    """ padding using tensor flow"""
    # input_list = pad_sequences(
    #     input_list, maxlen=2500, dtype='float32', padding='post', truncating='post')

    """padding using numpy for test with apple silicon"""
    if input_list[0].__len__() < 2500:
        input_list[0].extend([0] * (2500 - input_list[0].__len__())) # padding with 0
    else:
        input_list[0] = input_list[0][:2500]
    y_pred = svm.predict(input_list)
    logging.info("audio prediction result: " + str(y_pred[0]))
    return y_pred[0]


def combine_predict(audio_result: int, gesture_result: object) -> int:
    counts = np.bincount(gesture_result)
    gesture_max_result = np.argmax(counts)

    if gesture_max_result == audio_result:
        return gesture_max_result
    else:
        return int(gesture_max_result)


"""
Testing
"""
if __name__ == "__main__":
    audio_model, gesture_model = load_models()

    csv_file_path = "model_training/Gesture/4/1677727897634.csv"

    new_audio_path = "model_training/Audio/1/1677661942148.mp3"

    audio_result = predict_new_audio(new_audio_path, audio_model)

    gesture_result = predict_new_gesture(csv_file_path, gesture_model)

    combine_prediction = combine_predict(audio_result, gesture_result)

    print(audio_result)
    print(gesture_result)
    print(combine_prediction)
