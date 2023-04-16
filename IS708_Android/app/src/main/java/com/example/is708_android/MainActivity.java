package com.example.is708_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import android.media.MediaRecorder;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button recordButton;
    private boolean isRecording = false;
    private Timer timer;
    private  WebSocketClient webSocketClient;
    private File audioFile;

    MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the record button
        recordButton = findViewById(R.id.capture_audio_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    startRecordingAudio();
                } else {
                    stopRecordingAudio();
                }
            }
        });

        // request for audio recording permission
        requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        // request for file write permission
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    private void startRecordingAudio(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordButton.setText("Stop Recording");
            }
        });
        isRecording = true;

       timer = new Timer();
       timer.schedule(new TimerTask() {
           @Override
           public void run() {
               stopRecordingAudio();
           }
       }, 3000);

       // create the audio file
        try{
            audioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "audio.raw");
            audioFile.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }

        // Initialize MediaRecorder again, else will encounter java.lang.IllegalStateException error
        mediaRecorder = new MediaRecorder();

        // start recording Audio
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingAudio(){
        // change button text and reset isRecording flag
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordButton.setText("Record Audio");
                Toast.makeText(MainActivity.this, "Recording Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        isRecording = false;
        // Stop recording and release MediaRecorder
        mediaRecorder.stop();
        mediaRecorder.release();

        // cancel the timer
        timer.cancel();

        /*
        * Send audio file over webserver
        * */
//        try {
//            byte[] audioArrayByte = convertAudioToByteArray(audioFile);
//            sendDataOverWebSocket(audioArrayByte, "ws:localhost:8086");
//        }catch (IOException e){
//            e.printStackTrace();
//        }catch (URISyntaxException e){
//            e.printStackTrace();
//        }

    }

    private byte[] convertAudioToByteArray(File audioFile) throws IOException {
        // Create input stream from audio file
        FileInputStream inputStream = new FileInputStream(audioFile);

        // Create byte array to store audio data
        byte[] byteArray = new byte[(int) audioFile.length()];

        // Read audio data into byte array
        inputStream.read(byteArray);

        // Close input stream
        inputStream.close();

        return byteArray;
    }



    public void sendDataOverWebSocket(byte[] byteArray, String serverUrl) throws IOException, URISyntaxException {
        // Create a WebSocket client instance
        WebSocketClient webSocketClient = new WebSocketClient(new URI(serverUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WebSocket", "WebSocket connection opened");
            }

            //Modify onMessage function to do show the output from Python Server.
            @Override
            public void onMessage(String message) {
                Log.i("WebSocket", "Received message: " + message);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("WebSocket", "WebSocket connection closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error in WebSocket connection", ex);
            }
        };


        // Connect to the WebSocket server
        webSocketClient.connect();

        // Send the CSV data as a byte array over the WebSocket connection
        webSocketClient.send(byteArray);

        // Close the WebSocket connection
        webSocketClient.close();
    }

}