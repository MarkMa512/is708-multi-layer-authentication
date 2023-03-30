for i in *.raw; do ffmpeg -f s16le -ar 44100 -ac 1 -i "$i" "${i%.*}.mp3"; done

# for dir in .; do (cd "$dir" && mkdir raw_file && mv *.raw raw_file); done

mkdir raw_file; mv *.raw raw_file;