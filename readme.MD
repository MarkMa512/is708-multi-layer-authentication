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
    <a href="https://github.com/MarkMa512/is708-multi-layer-authentication">View Demo</a>
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
        <li><a href="#built-with">Built With</a></li>
        <li><a href="#directries">Directries</a></li>
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

### Directories

- `IS708_Android`: Android Front End Application  

- `IS708_API`: Python Backend Server  
  - `model_training`: Training data and Jupyter Note Booking for the Classification Model Building

- `media`: Photos and Screenshots  

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
2. Ensure [Python 3.9](https://www.python.org/downloads/release/python-3912/) or later is installed
    - Ensure [pip](https://pip.pypa.io/en/stable/installation/) is installed. 
    - Linux and macOS (Install using Terminal):  
        ```sh 
        python -m ensurepip --upgrade
        ```
    - Windows (Install using CMD):
        ```cmd
        py -m ensurepip --upgrade
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

2. run `raw_to_mp3.sh` to convert the raw format audio to MP3 format for training

```sh 
./raw_to_mp3.sh
```

3. Run `Audio Classification Notebook.ipynb` and `Gesture Classification Notebook.ipynb` respectively to create the models. Upon completion, the following models should be created in `model_training` directory: 
  - `audio_svm_model.pkl`
  - `gesture_random_forest_classifier_model_relative_time.pkl`
    - This model takes around 5-10 minutes to build, depends on your machine performance. 

### Pre-trained model 
The pretrained models can be download from [here](). Download them into `IS708_API/model_training` directory. 

### Launch Python Backend Server 

1. Enter the `IS708_API` folder with Terminal
```sh
cd IS708_API
```

2. Run `create_out_dir.sh` to create the output folder structure
```sh 
./create_out_dir.sh
```

3. Run the `server.py`
```sh
python server.py 
```
Note: You may need to use `python3` instead of just `python`. 

4. to test your server setup, run `test_client.py`: 

```sh 
python test_client.py
```
Do ensure that `sample.raw` is in folder `IS708_API/model_training`. The following output should be displayed: 

> yyyy-mm-dd hh:mm:14,240 - INFO - Connected to server.  
> yyyy-mm-dd hh:mm:14,241 - INFO - Sent 257544 bytes of audio data to the server.  
> yyyy-mm-dd hh:mm:14,242 - INFO - Sent 223684 bytes of gesture data to the server.  
> yyyy-mm-dd hh:mm:18,475 - INFO - Received response: b'3'  
> yyyy-mm-dd hh:mm:18,475 - INFO - ========== Server Setup Success! ==========  

5. To clear the `out` folder after testing, run `clear_out_folder.sh`: 

```sh
./clear_out_folder.sh
```

### Launch Android Application

1. Open Andorid Studio installed on your machine, open the project under `IS708_Android`. 
2. Connect your testing phone, ensure that the developing option and debugging mode is on. 
3. Enable the access to the server running on localhost: 

```sh 
adb reverse tcp:8086 tcp:8086
```

4. Record Audio by pressing the `Record Audio` button 
5. Record Gesture by pressing the `Record Gesture` button

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Troubleshoot

1. If you have permission deny error when executing the shell script, run the following command to add exexution power to the file. 

```sh
chmod +x shell_script_name.sh
```

2. As the programming of this project is done on macOS, if you have the error of `'\r': command not found` when running the script on Linux machine, perform the following command in your terminal: 

```sh 
sed -i 's/\r$//' *
```

<!-- ROADMAP -->
## Roadmap
Undergraduate / Master Student: 
- [ ] (C1) Building the base Android Application for recording and saving IMU and audio data.
- [x] (C2) Building a Speaker recognition model (Python code on server)
- [x] (C3) Building an IMU gesture pattern recognition model (Python code on server)
- [x] (C4) Combine the prediction results from the two models (Return the user ID with maximum joint class probability from C2 and C3)
- [ ] (C5) Complete integration of the app with the authentication result shown as a Toast message. (Send recorded input to python backend, retrieve the prediction result, compare the predicted user ID to the owner ID for authentication)
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

### Reference 
[Connecting Android Apps to localhost, Simplified](https://dev.to/tusharsadhwani/connecting-android-apps-to-localhost-simplified-57lm)

<p align="right">(<a href="#readme-top">back to top</a>)</p>