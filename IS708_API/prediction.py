import pandas as pd
import numpy as np
import librosa
import joblib
import logging

# set up logging
logging.basicConfig(
    format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)


def load_models() -> object:
    """
    Load the trained model for audio and gesture prediction.

    Returns:
        - audio_model (object): Trained SVM model for audio prediction
        - gesture_model (object): Trained Random Forest Classifier model for gesture prediction
    """
    logging.info("loading audio and gesture models... Please wait.")
    audio_model = joblib.load('model_training/audio_svm_model.pkl')
    gesture_model = joblib.load(
        'model_training/gesture_random_forest_classifier_model_row_by_row.pkl')
    logging.info("audio and gesture models are loaded!")
    return audio_model, gesture_model


def predict_new_gesture(csv_file_path: str, clf: object) -> int:
    """
    Read a CSV file of IMU sensor reading and predict the label for each line of reading using trained model clf

    Parameters:
        - csv_file_path (str): Path to the CSV contraining IMU sensor reading

    Returns:
        - result (int): the predicted userID for the given CSV file
    """

    df = pd.read_csv(csv_file_path)
    # Calculate the relative time difference for each row of reading
    # compared to the 1st row of reading in each CSV file / reading
    df['relative_time'] = df['Timestamp'].apply(
        lambda x: (x - df['Timestamp'][0]))
    df = df.drop(["Timestamp"], axis=1)  # Drop "Timestamp" columns as features
    # - result_list (numpy.ndarray): An array of predicted label for each row of reading in the CSV file
    result_list = clf.predict(df)
    frequncy_dict = {i: np.count_nonzero(
        result_list == i) for i in range(1, 6)}
    logging.info(f"===== gesture prediction result (User:Count): {frequncy_dict} =====")
    counts = np.bincount(result_list)
    result = np.argmax(counts)
    return result


def predict_new_gesture_proba(csv_file_path: str, clf: object) -> dict:
    """
    Read a CSV file of IMU sensor reading and produce probability for each user using trained model clf

    Parameters:
        - csv_file_path (str): Path to the CSV contraining IMU sensor reading

    Returns:
        - probability_dict (dict): {userID:probability}
    """
    df = pd.read_csv(csv_file_path)
    # Calculate the relative time difference for each row of reading
    # compared to the 1st row of reading in each CSV file / reading
    df['relative_time'] = df['Timestamp'].apply(
        lambda x: (x - df['Timestamp'][0]))
    df = df.drop(["Timestamp"], axis=1)  # Drop "Timestamp" columns as features
    # result_list (numpy.ndarray): An array of predicted label for each row of reading in the CSV file
    result_list = clf.predict(df)
    # frequncy_dict (dict): A dictionary of user and the number of times the user is predicted
    frequncy_dict = {i: np.count_nonzero(
        result_list == i) for i in range(1, 6)}
    # calculates the total number of rows in the CSV file / reading counts
    total_reading_counts = sum(frequncy_dict.values())
    # probability_dict (dict): A dictionary of user and the probability of the user
    # calculated by deviding the number of times the user is predicted by the total number of rows in the CSV file / reading counts
    probability_dict = {k: v / total_reading_counts for k,
                        v in frequncy_dict.items()}
    logging.info(f"----- gesture probability (User:Probability): {probability_dict} -----")
    return probability_dict


def predict_new_gesture_integral(csv_file_path: str, clf: object) -> int:
    """
    This function is retired as Row by Row model is used. 
    """

    """
    Read a CSV file of IMU sensor reading and predict the label for each line of reading using trained model clf (Integral)

    Parameters:
        - csv_file_path (str): Path to the CSV contraining IMU sensor reading

    Returns:
        - result (int): predicted userID for the given CSV file
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
    logging.info(f"===== gesture prediction result (User): {result_list[0]} =====")
    return result_list[0]


def predict_new_audio(new_audio_path: str, svm: object, n_mfcc=20) -> int:
    """
    Predict the source of new audio at new_audio_path based on the model svm trained. 

    Parameters:
        - new_audio_path (str): Path to the folder containing new audio to be predicted. 
        - svm: trained SVM model 
        - n_mfcc (int): Number of Mel-frequency cepstral coefficients (MFCC) to extract, default = 20. 

    Returns:
        - result_list[0](int): The predicted userID for the given new audio. 
    """
    input_list = []
    signal_new, sr_new = librosa.load(new_audio_path, duration=3.0)
    mfcc_new = librosa.feature.mfcc(y=signal_new, sr=sr_new, n_mfcc=n_mfcc)
    mfcc_flattened = mfcc_new.T.flatten().tolist()
    input_list.append(mfcc_flattened)

    # Padding / truncating the input list to ensure that they are consistent to the length of the training data
    """ By default: padding using tensor flow"""
    # from tensorflow.keras.preprocessing.sequence import pad_sequences
    # input_list = pad_sequences(
    #     input_list, maxlen=2500, dtype='float32', padding='post', truncating='post')

    """For Apple Silicon Mac: padding using numpy for test with apple silicon"""
    if input_list[0].__len__() < 2500:
        input_list[0].extend([0] * (2500 - input_list[0].__len__())) # padding with 0
    else:
        input_list[0] = input_list[0][:2500]

    result_list = svm.predict(input_list)
    logging.info("===== audio prediction result (User): " +
                 str(result_list[0]) + " =====")
    return result_list[0]


def predict_new_audio_proba(new_audio_path: str, svm: object, n_mfcc=20) -> dict:
    """
    Produce the probability of each user for the new audio at new_audio_path based on the model svm trained.

    Parameters:
        - new_audio_path (str): Path to the folder containing new audio to be predicted. 
        - svm: trained SVM model 
        - n_mfcc (int): Number of Mel-frequency cepstral coefficients (MFCC) to extract, default = 20. 

    Returns:
        - probabilities (dict):  {userID:probability}

    """
    input_list = []
    signal_new, sr_new = librosa.load(new_audio_path, duration=3.0)
    mfcc_new = librosa.feature.mfcc(y=signal_new, sr=sr_new, n_mfcc=n_mfcc)
    mfcc_flattened = mfcc_new.T.flatten().tolist()
    input_list.append(mfcc_flattened)

    # Padding / truncating the input list to ensure that they are consistent to the length of the training data
    """ By default: padding using tensor flow"""
    # from tensorflow.keras.preprocessing.sequence import pad_sequences
    # input_list = pad_sequences(
    #     input_list, maxlen=2500, dtype='float32', padding='post', truncating='post')

    """For Apple Silicon Mac: padding using numpy for test with apple silicon"""
    if input_list[0].__len__() < 2500:
        input_list[0].extend([0] * (2500 - input_list[0].__len__())) # padding with 0
    else:
        input_list[0] = input_list[0][:2500]

    result_list = svm.predict_proba(input_list)
    probability_dict = {k: v for k, v in enumerate(result_list[0], 1)}
    logging.info(f"----- audio probability (User:Probability): {probability_dict} -----")
    return probability_dict


"""
Fusion Logic
"""


def combine_predict(audio_result: int, gesture_result: int, new_audio_path: str, audio_model: object, csv_file_path: str, gesture_model: object) -> int:
    """
    Combine the prediction result from audio and gesture model and return the final prediction result.

    Parameters:
        - audio_result (int): The predicted userID for the given new audio.
        - gesture_result (int): The predicted userID for the given new gesture.
        - new_audio_path (str): Path to the folder containing new audio to be predicted.
        - audio_model: trained SVM model for audio
        - csv_file_path (str): Path to the folder containing new gesture to be predicted.
        - gesture_model: trained SVM model for gesture

    Returns:
        - If both models predict the same user, simply return the user.
        - max(joint_prob, key=joint_prob.get)(int): The predicted userID for the given new audio and gesture, with the highest joint probability.
    """
    # if both models predict the same user, simply return the user
    if (audio_result == gesture_result):
        return audio_result
    else:
        # otherwise, calculate the joint probability of each user
        # and return the user with the highest joint probability
        audio_prob = predict_new_audio_proba(
            new_audio_path, audio_model, n_mfcc=20)
        gesture_prob = predict_new_gesture_proba(
            csv_file_path, gesture_model)
        joint_prob = {}
        # for each user, calculate the joint probability
        for user in range(1, 6):
            # since the action of speaking does not affect the gesture performance,
            # and both models are trained independently, we assume that the joint probability is the product of the individual probabilities
            joint_prob[user] = audio_prob[user] * gesture_prob[user]
        logging.info(f"----- joint probability (User:Probability): {joint_prob} -----")
        return max(joint_prob, key=joint_prob.get)


"""
Testing
"""
if __name__ == "__main__":
    # load models
    audio_model, gesture_model = load_models()

    """
    Simulate a case where by the audio model predicts user 1, and the gesture model predicts user 4
    """
    print("===== Simulate a case where by the audio model predicts user 1, and the gesture model predicts user 4 =====")
    csv_file_path = "model_training/Gesture/4/1677727897634.csv"
    new_audio_path = "model_training/Audio/1/1677661942148.mp3"

    # obtain audio and gesture prediction results
    audio_result = predict_new_audio(new_audio_path, audio_model)
    gesture_result = predict_new_gesture(csv_file_path, gesture_model)

    # combine the prediction results
    combine_prediction = combine_predict(
        audio_result, gesture_result, new_audio_path, audio_model, csv_file_path, gesture_model)

    print(f'Audio Result: {audio_result}')
    print(f'Gesture Result: {gesture_result}')
    print(f'Combined Result: {combine_prediction}')
