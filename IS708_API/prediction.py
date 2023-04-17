import pandas as pd
import numpy as np
import librosa
import joblib
import logging

# set up logging
logging.basicConfig(
    format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)


def load_models() -> object:
    # Load the trained model
    logging.info("loading audio and gesture models... Please wait.")
    audio_model = joblib.load('model_training/audio_svm_model.pkl')
    gesture_model = joblib.load(
        'model_training/gesture_random_forest_classifier_model_integral.pkl')
    logging.info("audio and gesture models are loaded!")
    return audio_model, gesture_model

def predict_new_gesture(csv_file_path: str, clf: object) -> int:
    
    """
    Read a CSV file of IMU sensor reading and predict the label for each line of reading using trained model clf
    
    Parameters:
        - csv_file_path (str): Path to the CSV contraining IMU sensor reading
    
    Returns:
        - result (int): predicted user
    """
    
    df = pd.read_csv(csv_file_path)
    # Calculate the relative time difference for each row of reading
    # compared to the 1st row of reading in each CSV file / reading
    df['relative_time'] = df['Timestamp'].apply(lambda x: (x - df['Timestamp'][0]))
    df = df.drop(["Timestamp"], axis=1)  # Drop "Timestamp" columns as features
    result_list = clf.predict(df) # - result_list (numpy.ndarray): An array of predicted label for each row of reading in the CSV file
    frequncy_dict = {i: np.count_nonzero(result_list == i) for i in result_list}
    counts = np.bincount(result_list)
    result = np.argmax(counts)
    return result


def predict_new_gesture_integral(csv_file_path: str, clf: object) -> int:
    """
    Read a CSV file of IMU sensor reading and predict the label for each line of reading using trained model clf

    Parameters:
        - csv_file_path (str): Path to the CSV contraining IMU sensor reading

    Returns:
        - result (int): predicted user
    """
    input_list = []
    df = pd.read_csv(csv_file_path)
    # Calculate the relative time difference for each row of reading
    # compared to the 1st row of reading in each CSV file / reading
    df['relative_time'] = df['Timestamp'].apply(
        lambda x: (x - df['Timestamp'][0]))
    df = df.drop(["Timestamp"], axis=1)  # Drop "Timestamp" columns as features
    df = df.iloc[10:2710, :]  # standardize the length of the dataframe
    logging.info("df length" + str(len(df)))
    flattened_list = df.to_numpy().flatten().tolist()
    input_list.append(flattened_list)
    result_list = clf.predict(input_list)
    logging.info("===== gesture prediction result (User): " +
                 str(result_list[0]) + " =====")
    return result_list[0]


def predict_new_audio(new_audio_path: str, svm: object, n_mfcc=20) -> int:
    input_list = []
    signal_new, sr_new = librosa.load(new_audio_path, duration=3.0)
    mfcc_new = librosa.feature.mfcc(y=signal_new, sr=sr_new, n_mfcc=n_mfcc)
    mfcc_flattened = mfcc_new.T.flatten().tolist()
    input_list.append(mfcc_flattened)

    """ By default: padding using tensor flow"""
    from tensorflow.keras.preprocessing.sequence import pad_sequences
    input_list = pad_sequences(
        input_list, maxlen=2500, dtype='float32', padding='post', truncating='post')

    """For Apple Silicon Mac: padding using numpy for test with apple silicon"""
    # if input_list[0].__len__() < 2500:
    #     input_list[0].extend([0] * (2500 - input_list[0].__len__())) # padding with 0
    # else:
    #     input_list[0] = input_list[0][:2500]

    y_pred = svm.predict(input_list)
    logging.info("===== audio prediction result (User): " +
                 str(y_pred[0]) + " =====")
    return y_pred[0]


def combine_predict(audio_result: int, gesture_result: int) -> int:
    if (audio_result == gesture_result):
        return audio_result
    else:
        return 0


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
