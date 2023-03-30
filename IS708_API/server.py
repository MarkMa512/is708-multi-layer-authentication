import asyncio
import websockets
import subprocess

async def handle_message(websocket, path):
    while True:
        message = await websocket.recv()
        # Decode the received message as a byte array
        byte_array = bytearray(message)

        # Process the received input here.
        # Response will be the class ID of the predicted subject. I have hardcoded here. However, you can replace the response variable with your model output accordingly.
        response = "1"
        # Encode the string as bytes and send it back to the client
        await websocket.send(response.encode())

def convertRawAudio(filePath,outputPath='output.mp3'):
    cmd = ['ffmpeg', '-f', 's16le', '-ar', '44100', '-ac', '1', '-i', filePath, outputPath]
    subprocess.run(cmd)

async def main():
    # Start the WebSocket server on port 8765
    #convertRawAudio('data/sample.raw')
    async with websockets.serve(handle_message, "localhost", 8765):
        await asyncio.Future()  # Run forever

asyncio.run(main())

