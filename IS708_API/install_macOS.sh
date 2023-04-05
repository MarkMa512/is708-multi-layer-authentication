#! /bin/bash 

# install homebrew package manager
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"

brew install ffmpeg
brew install android-platform-tools

# pip install relevent packages
pip install -r requirements.txt