<!-- Adopted from: https://github.com/othneildrew/Best-README-Template -->

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

  <h3 align="center">IS708 Mobile and Distributed System</h3>

  <p align="center">Programming Assignment
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
    <li><a href="#roadmap">Roadmap</a></li>
    <!-- <li><a href="#contributing">Contributing</a></li> -->
    <!-- <li><a href="#license">License</a></li> -->
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

### Directries

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
    - Windows(Install using CMD):
        ```cmd
        py -m ensurepip --upgrade
        ```

### Installation

Enter the `IS708_API` folder with Terminal
```sh
cd IS708_API
```
Run `install_Linux` or `install_macOS` according to your Operating System. 

```sh
./install_macOS.sh
```
Kindly note that Homebrew Package Manager will be installed on macOS. 

```sh
./install_Linux.sh
```
Enter your sudo password when prompted. 
<!-- USAGE EXAMPLES -->
## Usage

### Launch Python Backend Server 

1. Enter the `IS708_API` folder with Terminal
```sh
cd IS708_API
```

2. Run the `server.py`
```sh
python server.py 
```
Note: You may need to use python3 in case python does not work. 

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

<!-- ROADMAP -->
## Roadmap

- [ ] (C1) Building the base Android Application for recording and saving IMU and audio data.
- [x] (C2) Building a Speaker recognition model (Python code on server)
- [x] (C3) Building an IMU gesture pattern recognition model (Python code on server)
- [ ] (C4) Combine the prediction results from the two models (Return the user ID with maximum joint class probability from C2 and C3)
- [ ] (C5) Complete integration of the app with the authentication result shown as a Toast message. (Send recorded input to python backend, retrieve the prediction result, compare the predicted user ID to the owner ID for authentication)
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


<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

<p align="right">(<a href="#readme-top">back to top</a>)</p>