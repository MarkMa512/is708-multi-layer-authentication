#! /bin/bash 

# Define the parent directory where the subfolders are located
parent_dir=Audio;

# Loop through each subfolder
for folder in "${parent_dir}"/*; do
    # Check if the subfolder is a directory
    if [[ -d "${folder}" ]]; then
        # Loop through each raw file in the subfolder
        for raw_file in "${folder}"/*.raw; do
            if [[ -f "${raw_file}" ]]; then
                # Convert the raw file to mp3 using ffmpeg
                ffmpeg -f s16le -ar 44100 -ac 1 -i "${raw_file}" "${raw_file%.*}.mp3"
                # Remove the raw file
                rm "${raw_file}"
            fi
        done
    fi
done;

echo "Format Conversion Coversion Completed! "; 