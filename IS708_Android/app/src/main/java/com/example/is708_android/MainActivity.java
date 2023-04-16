package com.example.is708_android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.widget.Button;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Variables for recording audio
    private Button recordAudioButton;
    private boolean isRecording = false;
    private Timer timer;
    private File audioFile;

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSize;
    private AudioRecord audioRecord;
    private Thread recordingThread;

    // Variables for recording gesture
    private Button recordGestureButton;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private boolean isRecordingGesture = false;

    private final List<String> gestureData = Collections.synchronizedList(new ArrayList<>());
    private static final long RECORDING_DURATION_MS = 3000;
    private static final long SAMPLING_INTERVAL_MS = 1;

    // Variables for sending data over websocket
    private final String SERVER_URL = "ws://10.0.2.2:8086";

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the record audio button
        recordAudioButton = findViewById(R.id.capture_audio_button);
        recordAudioButton.setOnClickListener(view -> {
            if (!isRecording) {
                startRecordingAudio();
            } else {
                stopRecordingAudio();
            }
        });

        // request for audio recording permission
        requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        // request for file write permission
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // Initialize the record gesture button
        recordGestureButton = findViewById(R.id.capture_gesture_button);
        recordGestureButton.setOnClickListener(view -> {
            if (!isRecordingGesture) {
                startRecordingGesture();
            } else {
                stopRecordingGesture();
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

    @SuppressLint("SetTextI18n")
    private void startRecordingAudio() {
        runOnUiThread(() -> recordAudioButton.setText("Stop Recording"));
        isRecording = true;

        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MainActivity", "Audio recording permission not granted");
            return;
        }

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        // Create the audio file
        try {
            audioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "audio.raw");
            boolean audioFileCreation = audioFile.createNewFile();
            if (!audioFileCreation) {
                Log.i("MainActivity", "Audio file creation failed. This may be because the file already exists and it will be overwritten.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioRecord.startRecording();
        Toast.makeText(this, "Audio Recording started", Toast.LENGTH_SHORT).show();

        recordingThread = new Thread(() -> {
            try (FileOutputStream fos = new FileOutputStream(audioFile)) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);

                while (isRecording) {
                    int bytesRead = audioRecord.read(buffer, bufferSize);
                    fos.write(buffer.array(), 0, bytesRead);
                    buffer.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        recordingThread.start();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopRecordingAudio();
            }
        }, 3000);
    }

    @SuppressLint("SetTextI18n")
    private void stopRecordingAudio() {
        // Change button text and reset isRecording flag
        runOnUiThread(() -> recordAudioButton.setText("Record Audio"));

        isRecording = false;

        // Stop recording and release AudioRecord
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        // Stop recording thread
        if (recordingThread != null) {
            try {
                recordingThread.join();
                recordingThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Cancel the timer
        timer.cancel();

        runOnUiThread(() -> Toast.makeText(this, "Audio Recording Stopped. Data saved to 'Download'.", Toast.LENGTH_SHORT).show());

        /*
         * Send audio file over webserver
         * */

        try {
            byte[] audioArrayByte = convertFileToByteArray(audioFile);
            sendDataOverWebSocket(audioArrayByte, SERVER_URL);
        }catch (IOException | URISyntaxException e){
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void startRecordingGesture() {

        gestureData.clear();
        isRecordingGesture = true;
        runOnUiThread(() -> {
            recordGestureButton.setText("Stop Gesture Recording");
            Toast.makeText(MainActivity.this, "Gesture Recording Started", Toast.LENGTH_SHORT).show();
        });

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopRecordingGesture();
            }
        }, RECORDING_DURATION_MS);
    }

    @SuppressLint("SetTextI18n")
    private void stopRecordingGesture() {
        isRecordingGesture = false;
        runOnUiThread(() -> {
            recordGestureButton.setText("Record Gesture");
            Toast.makeText(MainActivity.this, "Gesture Recording Stopped. Data saved to 'Download'.", Toast.LENGTH_SHORT).show();
        });
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);

        try {
            saveGestureDataToCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File gestureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "gesture.csv");
            byte[] gestureArrayByte = convertFileToByteArray(gestureFile);
            sendDataOverWebSocket(gestureArrayByte, SERVER_URL);
        }catch (IOException | URISyntaxException e){
            e.printStackTrace();
        }

    }

    private void saveGestureDataToCSV() throws IOException {
        File gestureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "gesture.csv");
        try (FileWriter writer = new FileWriter(gestureFile)) {
            writer.write("Timestamp,AccelX,AccelY,AccelZ,GyroX,GyroY,GyroZ\n");
            synchronized (gestureData) {
                for (String line : gestureData) {
                    writer.write(line);
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double accelX = 0, accelY = 0, accelZ = 0;
        double gyroX = 0, gyroY = 0, gyroZ = 0;
        if (isRecordingGesture) {
            long timestamp = SystemClock.elapsedRealtimeNanos();
            int sensorType = event.sensor.getType();
            synchronized (gestureData) {
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    accelX = event.values[0];
                    accelY = event.values[1];
                    accelZ = event.values[2];
                } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
                    gyroX = event.values[0];
                    gyroY = event.values[1];
                    gyroZ = event.values[2];
                }

                if (sensorType == Sensor.TYPE_ACCELEROMETER || sensorType == Sensor.TYPE_GYROSCOPE) {
                    String line = String.format(Locale.US, "%d,%.7f,%.7f,%.7f,%.7f,%.7f,%.7f\n", timestamp, accelX, accelY, accelZ, gyroX, gyroY, gyroZ);
                    gestureData.add(line);
                    try {
                        Thread.sleep(SAMPLING_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }


    private byte[] convertFileToByteArray(File audioFile) throws IOException {
        // Create input stream from a file
        FileInputStream inputStream = new FileInputStream(audioFile);

        // Create byte array to store the data
        byte[] byteArray = new byte[(int) audioFile.length()];

        // Read audio data into byte array
        int bytesRead = inputStream.read(byteArray);

        if (bytesRead != byteArray.length) {
            Log.e("MainActivity", "Failed to read the entire audio file");
        }

        // Close input stream
        inputStream.close();

        return byteArray;
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
                String overallMessage;

                // if the message is of length 1, it is one of the user id (1,2,3,4,5)
                if (message.length() == 1){
                    overallMessage = "User " + message + " has been authenticated";
                }else{
                    // else it is the error message
                    overallMessage = message;
                }

                runOnUiThread(() -> Toast.makeText(MainActivity.this, overallMessage, Toast.LENGTH_SHORT).show());
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
            if (!connectionLatch.await(20, TimeUnit.SECONDS)) {
                Log.e("WebSocket", "WebSocket connection timeout");
                webSocketClient.close();
                return;
            }

            // Send the data as a byte array over the WebSocket connection
            webSocketClient.send(byteArray);
            Log.i("WebSocket", "Sent byteArray of length: " + byteArray.length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}