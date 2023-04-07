#! /bin/bash 

cd out # go into 'out' folder
cd Audio # # go into 'Audio' folder
rm * # remove all files in 'Audio' folder
cd ../Gesture # change directory to 'Gesture' folder
rm * # remove all files in 'Gesture' folder
echo "All files cleared in /out and its subfolders" 