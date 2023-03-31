# covert each raw file too mp3 file 
for i in *.raw; do ffmpeg -f s16le -ar 44100 -ac 1 -i "$i" "${i%.*}.mp3"; done

# for dir in .; do (cd "$dir" && mkdir raw_file && mv *.raw raw_file); done

# move all raw file to the raw_file dir
mkdir raw_file; mv *.raw raw_file;