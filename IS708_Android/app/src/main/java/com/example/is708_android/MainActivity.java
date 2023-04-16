package com.example.is708_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import android.media.MediaRecorder;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Variables for recording audio
    private Button recordAudioButton;
    private boolean isRecording = false;
    private Timer timer;
    private  WebSocketClient webSocketClient;
    private File audioFile;

    MediaRecorder mediaRecorder;

    // Variables for recording gesture
    private Button recordGestureButton;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private boolean isRecordingGesture = false;
    private File gestureFile;
    private FileWriter fileWriter;
    private long startTime;

    // Variables for sending data over websocket
    private String serverUrl = "ws://10.0.2.2:8086";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the record audio button
        recordAudioButton = findViewById(R.id.capture_audio_button);
        recordAudioButton.setOnClickListener(new View.OnClickListener() {
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

        // Initialize the record gesture button
        recordGestureButton = findViewById(R.id.capture_gesture_button);
        recordGestureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecordingGesture) {
                    startRecordingGesture();
                } else {
                    stopRecordingGesture();
                }
            }
        });

        // Initialize SensorManager and Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // request for sensor permission
        requestPermissions(new String[]{android.Manifest.permission.BODY_SENSORS}, 1);
        //declare the normal permission HIGH_SAMPLING_RATE_SENSORS.
        requestPermissions(new String[]{android.Manifest.permission.HIGH_SAMPLING_RATE_SENSORS}, 1);
    }

    private void startRecordingAudio(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordAudioButton.setText("Stop Audio Recording");
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
            Toast.makeText(this, "Audio Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingAudio(){
        // change button text and reset isRecording flag
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordAudioButton.setText("Record Audio");
                Toast.makeText(MainActivity.this, "Audio Recording Stopped. Data Saved. ", Toast.LENGTH_SHORT).show();
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

        try {
            byte[] audioArrayByte = convertFileToByteArray(audioFile);
            sendDataOverWebSocket(audioArrayByte, serverUrl);
        }catch (IOException e){
            e.printStackTrace();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

    }

    private byte[] convertFileToByteArray(File audioFile) throws IOException {
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

    private void startRecordingGesture() {
        isRecordingGesture = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordGestureButton.setText("Stop Gesture Recording");
                Toast.makeText(MainActivity.this, "Gesture Recording Started", Toast.LENGTH_SHORT).show();
            }
        });

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopRecordingGesture();
            }
        }, 3000);

        try {
            // Create CSV file
            gestureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "gesture.csv");
            gestureFile.createNewFile();

            // Initialize FileWriter
            fileWriter = new FileWriter(gestureFile);

            // Write CSV headers
            fileWriter.write("Timestamp,AccelX,AccelY,AccelZ,GyroX,GyroY,GyroZ\n");

            // Register sensor event listeners
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

            // Record start time
            startTime = SystemClock.elapsedRealtime();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecordingGesture() {
        isRecordingGesture = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recordGestureButton.setText("Record Gesture");
                Toast.makeText(MainActivity.this, "Gesture Recording Stopped. Data saved", Toast.LENGTH_SHORT).show();
            }
        });


        // Unregister sensor event listeners
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);

        try {
            // Close FileWriter
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        // cancel the timer
        timer.cancel();

        try {
            byte[] gestureArrayByte = convertFileToByteArray(gestureFile);
            sendDataOverWebSocket(gestureArrayByte, serverUrl);
        }catch (IOException e){
            e.printStackTrace();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRecordingGesture) {
            try {
                // Calculate elapsed time
//                long elapsedTime = SystemClock.elapsedRealtime() - startTime;
                // Get the current time stamp.
                long timestamp = event.timestamp;

                // Write sensor data to CSV file
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    fileWriter.write(timestamp + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + ",");
                } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    fileWriter.write(event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }


    public void sendDataOverWebSocket(byte[] byteArray, String serverUrl) throws IOException, URISyntaxException {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        // Create a WebSocket client instance
        WebSocketClient webSocketClient = new WebSocketClient(new URI(serverUrl)) {
            @Override
            public void onOpen(ServerHandshake handShakeData) {
                Log.i("WebSocket", "WebSocket connection opened");
                connectionLatch.countDown();
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

        try {
            // Wait for the connection to be established
            if (!connectionLatch.await(5, TimeUnit.SECONDS)) {
                Log.e("WebSocket", "WebSocket connection timeout");
                webSocketClient.close();
                return;
            }

            // Send the CSV data as a byte array over the WebSocket connection
            webSocketClient.send(byteArray);
            Log.i("WebSocket", "Sent byteArray of length: " + byteArray.length);

            // Close the WebSocket connection
            webSocketClient.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Close the WebSocket connection
        webSocketClient.close();
    }

}