package com.example.is708_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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