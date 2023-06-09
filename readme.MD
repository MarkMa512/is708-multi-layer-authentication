<!-- Template Adopted from: https://github.com/othneildrew/Best-README-Template -->

<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/MarkMa512/is708-multi-layer-authentication">
    <!-- <img src="images/logo.png" alt="Logo" width="80" height="80"> -->
  </a>

  <h3 align="center">Multi-layer Authentication System</h3>

  <p align="center">IS708 Mobile and Distributed System Programming Assignment
    <br />
    <a href="https://github.com/MarkMa512/is708-multi-layer-authentication"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="#quick-demo">View Demo</a>
    ·
    <a href="https://github.com/MarkMa512/is708-multi-layer-authentication/issues">Report Bug</a>
    ·
    <a href="https://github.com/MarkMa512/is708-multi-layer-authentication/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#quick-demo">Quick Demo</a></li>
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#directories">Directries</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#troubleshoot">Troubleshoot</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <!-- <li><a href="#contributing">Contributing</a></li> -->
    <!-- <li><a href="#license">License</a></li> -->
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This project aims to demonstrate the concept of Multi-layered Aunthentication. 
- The user will record a 3 second audio and moves his phone along a unique pattern in a 3-second window. 
- Both Audio and Inertial Measurement Unit (IMU) data will be recorded by the Android Application and transferred to the backend server. 
- The backend server runs machine learning model to determine if the user is one of the user to be authenticated. 

**Notice: This repositpry is Private on github until the course is over. Thank you.**

### Quick Demo


https://user-images.githubusercontent.com/42110231/232319506-63827e51-b087-43ea-bdd7-41270ad0e759.mov



### Directories

- `IS708_Android`: Android Front End Application  

- `IS708_API`: Python Backend Server  
  - `model_training`: Training data and Jupyter Note Booking for the Classification Model Building

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Built With

- [Android Studio](https://developer.android.com/studio)
- [Python 3.9.12](https://www.python.org/downloads/release/python-3912/)
    - [librosa](https://librosa.org/doc/latest/index.html)
    - [NumPy](https://numpy.org/)
    - [pandas](https://pandas.pydata.org/)
    - [scikit-learn](https://scikit-learn.org/stable/)
    - [TensorFlow](https://www.tensorflow.org/) 

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started


### Prerequisites

1. Ensure [Android Studio](https://developer.android.com/studio) is installed. 
2. Ensure [Python 3.9.12](https://www.python.org/downloads/release/python-3912/) or later is installed
    - Ensure [pip](https://pip.pypa.io/en/stable/installation/) is installed. 
    - Linux and macOS (Install using Terminal):  
        ```sh 
        python -m ensurepip --upgrade
        ```

### Installation

1. Clone the repository
```sh
git-clone https://github.com/MarkMa512/is708-multi-layer-authentication.git
```

2. Enter the `IS708_API` folder with Terminal
```sh
cd IS708_API
```
3. Run `install_Linux.sh` or `install_macOS.sh` according to your Operating System. Enter your sudo password when prompted. 

```sh
./install_macOS.sh
```
Note that the following softwares will be installed on macOS (as well as the required dependencies), in addition to the python libraraies. 
  - [Homebrew Package Manager](https://brew.sh)
  - android-platform-tools
  - [ffmpeg](https://www.ffmpeg.org)

```sh
./install_Linux.sh
```
<!-- USAGE EXAMPLES -->
## Usage

### Model Training
1. Unzip the `train_data.zip` into `IS708_API/model_training` directory. Ensure that the Directory structure is as follows: 

```
  ├── IS708_API  
  │   ├── model_training  
  │   │   ├── Audio  
  │   │   │   ├── 1  
  │   │   │   │   ├── xxxx.raw 
  │   │   │   │   ├── ... 
  │   │   │   ├── 2  
  │   │   │   ├── 3  
  │   │   │   ├── 4  
  │   │   │   ├── 5  
  │   │   ├── Gesture  
  │   │   │   ├── 1  
  │   │   │   │   ├── xxxx.csv
  │   │   │   │   ├── ...
  │   │   │   ├── 2  
  │   │   │   ├── 3  
  │   │   │   ├── 4  
  │   │   │   ├── 5  
  │   │   ├── Audio Classification Notebook.ipynb  
  │   │   ├── Gesture Classification Notebook.ipynb  
  │   │   ├── raw_to_mp3.sh  
  │   │   ├── sample.raw  
```

2. run `raw_to_mp3.sh` to convert the raw format audio to MP3 format for model training

```sh 
./raw_to_mp3.sh
```

3. Run `Audio Classification Notebook.ipynb` and `Gesture Classification Notebook (Row by Row).ipynb` respectively to create the models. Upon completion, the following models should be created in `model_training` directory: 
  - `audio_svm_model.pkl`
  - `gesture_random_forest_classifier_model_row_by_row.pkl`
    - This model takes around 5-10 minutes to build, depends on your machine performance. 

### Pre-trained model 
The pretrained models can be download from [here](https://drive.google.com/drive/folders/1EjJP8h2Ok38Dce6bwFOSEdQEYkxRHTn7?usp=sharing). **SMU Google Acount Login is required**. Put the 2 models into `IS708_API/model_training` directory. 

### Launch Python Backend Server 

1. Enter the `IS708_API` folder with Terminal
```sh
cd IS708_API
```

2. Run the `server.py`
```sh
python server.py 
```
Note: You may need to use `python3` instead of just `python`, depending on your machine setup. 

3. To test your server setup, run `test_client.py`: 

```sh 
python test_client.py
```
The following output should be displayed: 

> yyyy-mm-dd hh:mm:14,240 - INFO - Connected to server.  
> yyyy-mm-dd hh:mm:14,241 - INFO - Sent 257544 bytes of audio data to the server.  
> yyyy-mm-dd hh:mm:14,242 - INFO - Sent 223684 bytes of gesture data to the server.  
> yyyy-mm-dd hh:mm:18,475 - INFO - Received response: 1  
> yyyy-mm-dd hh:mm:18,475 - INFO - ========== Server Setup Success! ========== 

4. The `prediction.py` can be run independently for a testcase.  

```sh 
python prediction.py
```

The following output should be displayed: 
>yyyy-mm-dd hh:mm:31,113 - INFO - loading audio and gesture models... Please wait.  
>yyyy-mm-dd hh:mm:31,623 - INFO - audio and gesture models are loaded!  
>===== Simulate a case where by the audio model predicts user 1, and the gesture model predicts user 4 =====  
>yyyy-mm-dd hh:mm:34,004 - INFO - ===== audio prediction result (User): 1 =====  
>yyyy-mm-dd hh:mm:34,060 - INFO - ===== gesture prediction result (User:Count): {1: 0, 2: 0, 3: 0, 4: 2840, 5: 0} =====  
>yyyy-mm-dd hh:mm:34,069 - INFO - ----- audio probability (User:Probability): {1: 0.94295725011915, 2: 0.0375167850324598, 3: 0.0053484973854087394, 4: 0.0038162213458059436, 5: 0.010361246117175286}-----  
>yyyy-mm-dd hh:mm:34,116 - INFO - ----- gesture probability (User:Probability): {1: 0.0, 2: 0.0, 3: 0.0, 4: 1.0, 5: 0.0}-----  
>yyyy-mm-dd hh:mm:34,116 - INFO - ----- joint probability (User:Probability): {1: 0.0, 2: 0.0, 3: 0.0, 4: 0.0038162213458059436, 5: 0.0}-----  
>Audio Result: 1  
>Gesture Result: 4  
>Combined Result: 4  

### Launch Android Application

1. Open Andorid Studio installed on your machine, open the project under `IS708_Android`. 
2. Create a emulator, choosing model Pixel XL API 33 (Andoid Version 13). 
3. Run the application. 
  - Should the application ask for Audio Record / Mic Access Permission, please grant the permission for it to properly function. 
  > Allow IS708_Android to record audio?
  - Please select: `While using the app` option
4. Record Audio by pressing the `Record Audio` button. 
5. Record Gesture by pressing the `Record Gesture` button. 
  - The recorded audio file `audio.raw` and recorded IMU file `gesture.csv` will be saved to the Download Directory inside your Android Emulator. You can access them via the Phone’s File folder, or using adb shell. 

  ```sh
  adb shell
  ```

  ```sh
  cd /sdcard/Download
  ```

  - list all the files 
  ```sh
  ls -la
  ```

  - inspect csv content
  ```
  cat gesture.csv
  ```
6. Prediction result will be shown as a Toast Message. 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Troubleshoot

1. If you have permission deny error when executing the shell script, run the following command to add exexution power to the file. 

```sh
chmod +x shell_script_name.sh
```

2. As the programming of this project is done on macOS, if you have the error of `'\r': command not found` when running the script on Linux machine, perform the following command in your terminal in the directory containing the files: 

```sh 
sed -i 's/\r$//' *
```

3. If you encountered the following error while trying to install the relevant package via pip: 

> ERROR: Could not find a version that satisfies the requirement subprocess (from versions: none)
> ERROR: No matching distribution found for subprocess

Try upgrading your pip to the latest version: 

```sh
python -m pip install --upgrade pip
```

4. If you encounter the error of `The kernel appears to have died. It will restart automatically.` while running the `Audio Classification Notebook.ipynb`, this is likely due to that `tensorflow` package was not installed correctly. Or it could be that you are using a Apple Silicon Mac. Please try on a Intel-based Mac. 

```sh
pip uninstall tensorflow
```
```sh
pip install tensorflow 
```
5. If you encounter the error of `illegal hardware instruction` after running `python server.py `, or continue to experience the error of `The kernel appears to have died. It will restart automatically.`, this is becuase of python was not correctly installed on your Apple Silicon Mac (ie, you have installed Python intended for an Intel Mac). Please try on a Intel-based Mac Instead. To by-pass this issue, you can ajust the following lines in `prediction.py`, in `predict_new_audio_proba()` function and `predict_new_audio()`function to disable padding using Tensor Flow, and use Numpy for the padding. **However, this may affects the accuracy of the prediction.**

```py
    """ By default: padding using tensor flow"""
    # from tensorflow.keras.preprocessing.sequence import pad_sequences
    # input_list = pad_sequences(
    #     input_list, maxlen=2500, dtype='float32', padding='post', truncating='post')

    """For Apple Silicon Mac: padding using numpy for test with apple silicon"""
    if input_list[0].__len__() < 2500:
        input_list[0].extend([0] * (2500 - input_list[0].__len__())) # padding with 0
    else:
        input_list[0] = input_list[0][:2500]
```

6. Should you have any further python library/compatibility issue, you can install the required libary with versions specified: 

```sh 
pip install -r requirement_version.txt
```

<!-- ROADMAP -->
## Roadmap
Undergraduate / Master Student: 
- [x] (C1) Building the base Android Application for recording and saving IMU and audio data.
- [x] (C2) Building a Speaker recognition model (Python code on server)
- [x] (C3) Building an IMU gesture pattern recognition model (Python code on server)
- [x] (C4) Combine the prediction results from the two models (Return the user ID with maximum joint class probability from C2 and C3)
- [x] (C5) Complete integration of the app with the authentication result shown as a Toast message. (Send recorded input to python backend, retrieve the prediction result, compare the predicted user ID to the owner ID for authentication)  

PHD Student:  
- [ ] (C6) Building the unified multi-modal recognition model. (Single model with both inputs instead of 2 separate models. Needs to be implemented on python server offline)

See the [open issues](https://github.com/MarkMa512/is708-multi-layer-authentication/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->
<!-- ## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p> -->



<!-- LICENSE -->
<!-- ## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p> -->



<!-- CONTACT -->
## Contact
[Ma Ningzhi](https://github.com/MarkMa512)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

Readme Template adopted from [Best-README-Template](https://github.com/othneildrew/Best-README-Template)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
