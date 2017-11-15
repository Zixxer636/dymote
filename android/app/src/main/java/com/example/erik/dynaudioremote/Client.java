package com.example.erik.dynaudioremote;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataOutputStream socketOutput;
    private BufferedReader socketInput;
    private BufferedInputStream bin;

    private String ip;
    private int port;
    private ClientCallback listener=null;

    private int volume = -1;
    private int source = -1;
    private Boolean muted = false;
    private Boolean powerOn = false;

    public Client(String ip, int port){
        this.ip=ip;
        this.port=port;
    }

    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
                try {
                    socket.connect(socketAddress);
                    socketOutput = new DataOutputStream(socket.getOutputStream());
                   // din = new DataInputStream(socket.getInputStream());
                    bin = new BufferedInputStream(socket.getInputStream());

                    socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    new ReceiveThread().start();

                    if(listener!=null)
                        listener.onConnect(socket);
                } catch (IOException e) {
                    if(listener!=null)
                        listener.onConnectError(socket, e.getMessage());
                }
            }
        }).start();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    public void send(Command command){
        byte[] myList = {
                (byte)0xFF, 0x55, 0x05, 0x2F, (byte)0xA0, 0, 0, 0x31, 0
        };
        Log.i("Write", "");
        Log.i("Write", command.toString());

        switch (command) {
            case POWER_ON:
                myList[5] = 0x01;
                break;
            case POWER_OFF:
                myList[5] = 0x02;
                break;
            case MUTE:
                myList[5] = 0x12;
                break;
            case VOLUME_UP:
                volume++;

                myList[5] = 0x13;
                myList[6] = (byte)volume;

                listener.onVolumeChanged(volume);
                break;
            case VOLUME_DOWN:
                volume--;

                myList[5] = 0x14;
                myList[6] = (byte)volume;

                listener.onVolumeChanged(volume);
                break;
            case SOURCE_LINE:
                source = 2;

                myList[5] = 0x15;
                myList[6] = (byte)source;

                listener.onSourceChanged(source);
                break;
            case SOURCE_OPTICAL:
                source = 3;

                myList[5] = 0x15;
                myList[6] = (byte)source;

                listener.onSourceChanged(source);
                break;
            case SOURCE_COAX:
                source = 4;

                myList[5] = 0x15;
                myList[6] = (byte)source;

                listener.onSourceChanged(source);
                break;
            case SOURCE_USB:
                source = 5;

                myList[5] = 0x15;
                myList[6] = (byte)source;

                listener.onSourceChanged(source);
                break;
        }

        int sum = 0;
        for (int i = 3; i < 8; i++) {
            sum += myList[i];
        }

        int q = (int)(sum/255.00 + 0.5); // Rounded up sum / 255
        int checksum = q*255-sum-(5-q);

        if (checksum < 0) {
            checksum = checksum & ((1 << 8) - 1);
        }

        myList[8] = (byte)checksum;

        try {
            Log.i("Write", bytesToHex(myList));

            socketOutput.write(myList);
            socketOutput.flush();
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    private class ReceiveThread extends Thread implements Runnable{
        public void run(){
            try {
                byte[] buffer = new byte[14];


                // I/Read: FF 55 0A     31 52 5D 01 06      03      01       00    00 01 0A FF 55 0A 31 52 39 01 05 03 01 00 00 03 2D FF 55 0A 31 52 5D 01 05 03 01 00 00 01 0B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
                //               Type               Volume  Input   Zone?    Mute
                while (true) {
                    // TODO: Rename btrar to something more meaningful
                    int bytesRead = bin.read(buffer);
                    // Do something with the data...

                    Log.i("Read", Integer.toString(bytesRead));
                    Log.i("Read", bytesToHex(buffer));

                    if (buffer[2] == 0x0A) { // probably messages that should go to the speakers
                        // Volume
                        int newVolume = buffer[7];
                        if (volume != newVolume) {
                            listener.onVolumeChanged(newVolume);
                            volume = newVolume;
                        }

                        int newSource = buffer[8];
                        if (source != newSource) {
                            listener.onSourceChanged(newSource);
                            source = newSource;
                        }

                        // Power
                        Boolean newPowerOn = buffer[9] == 0x01;
                        if (powerOn != newPowerOn) {
                            listener.onPowerChanged(newPowerOn);
                            powerOn = newPowerOn;
                        }

                        // Mute
                        Boolean newMuted = buffer[10] == 0x01;
                        if (muted != newMuted) {
                            listener.onMuteChanged(newMuted);
                            muted = newMuted;
                        }

                        /*
07-02 22:06:28.391 25673-25721/com.example.erik.dynaudioremote I/Read: 14
07-02 22:06:28.391 25673-25721/com.example.erik.dynaudioremote I/Read: 0A 31 52 39 01 03 04 01 01 00 03 2D FF 55
07-02 22:06:28.391 25673-25721/com.example.erik.dynaudioremote I/Read: 12
07-02 22:06:28.392 25673-25721/com.example.erik.dynaudioremote I/Read: 0A 31 52 5D 01 03 04 01 01 00 01 0B FF 55
07-02 22:06:28.392 25673-25673/com.example.erik.dynaudioremote I/onMuteChanged: true


// turn off
07-03 19:32:33.693 3279-3279/com.example.erik.dynaudioremote I/onVolumeChanged: 7
07-03 19:32:37.639 3279-3341/com.example.erik.dynaudioremote I/Read: 14
07-03 19:32:37.639 3279-3341/com.example.erik.dynaudioremote I/Read: FF 55 0A 31 52 5D 01 07 03 01 00 00 01 09
07-03 19:32:37.639 3279-3279/com.example.erik.dynaudioremote I/onVolumeChanged: 7
07-03 19:32:37.715 3279-3341/com.example.erik.dynaudioremote I/Read: 14
07-03 19:32:37.715 3279-3341/com.example.erik.dynaudioremote I/Read: FF 55 0A 31 52 39 01 07 03 01 00 00 03 2B
07-03 19:32:37.715 3279-3279/com.example.erik.dynaudioremote I/onVolumeChanged: 7
07-03 19:32:38.448 3279-3341/com.example.erik.dynaudioremote I/Read: 12
07-03 19:32:38.448 3279-3341/com.example.erik.dynaudioremote I/Read: FF 55 08 2E A0 02 00 21 00 00 D9 2E 03 2B
07-03 19:32:38.448 3279-3279/com.example.erik.dynaudioremote I/onPowerChanged: false
07-03 19:32:41.659 3279-3341/com.example.erik.dynaudioremote I/Read: 14
07-03 19:32:41.659 3279-3341/com.example.erik.dynaudioremote I/Read: FF 55 0A 31 52 5D 01 07 03 01 00 00 01 09
07-03 19:32:41.659 3279-3279/com.example.erik.dynaudioremote I/onVolumeChanged: 7
07-03 19:32:43.552 3279-3341/com.example.erik.dynaudioremote I/Read: 12
07-03 19:32:43.553 3279-3341/com.example.erik.dynaudioremote I/Read: FF 55 08 2E A0 02 00 21 00 00 D9 2E 01 09
07-03 19:32:43.553 3279-3279/com.example.erik.dynaudioremote I/onPowerChanged: false
07-03 19:32:46.658 3279-3341/com.example.erik.dynaudioremote I/Read: 14
07-03 19:32:46.659 3279-3341/com.example.erik.dynaudioremote I/Read: FF 55 0A 31 52 5D 01 07 03 00 00 03 01 07
07-03 19:32:46.659 3279-3279/com.example.erik.dynaudioremote I/onVolumeChanged: 7
07-03 19:32:46.659 3279-3279/com.example.erik.dynaudioremote I/onPowerChanged: false

// turn on
07-03 19:33:20.674 4909-4927/com.example.erik.dynaudioremote I/Read: 12
07-03 19:33:20.675 4909-4927/com.example.erik.dynaudioremote I/Read: FF 55 08 2E A0 01 00 21 00 00 D9 2F 00 00
07-03 19:33:20.675 4909-4909/com.example.erik.dynaudioremote I/onPowerChanged: true
07-03 19:33:22.305 4909-4927/com.example.erik.dynaudioremote I/Read: 14
07-03 19:33:22.305 4909-4927/com.example.erik.dynaudioremote I/Read: FF 55 0A 31 52 5D 01 07 03 01 00 00 01 09
07-03 19:33:22.305 4909-4909/com.example.erik.dynaudioremote I/onVolumeChanged: 7
                         */
                    }

                    if (buffer[2] == 0x08) { // Seems to be the remote...

                        // FF 55 08 2E A0 01 00 21 00 00 D9 2F 00 00 = ON
                        // FF 55 08 2E A0 02 00 21 00 00 D9 2E 01 0C = OFF
                        // FF 55 08 2E A0 01 00 21 00 00 D9 2F 01 0A = ON
                        // FF 55 08 2E A0 04 05 21 00 00 D9 27 01 0C = VOL UP
                        // FF 55 08 2E A0 04 06 21 00 00 D9 26 01 0C = VOL UP
                        // FF 55 08 2E A0 05 03 21 00 00 D9 28 01 0C = VOL DOWN
                        // FF 55 08 2E A0 03 01 21 00 00 D9 2C 03 2E = MUTE
                        // FF 55 08 2E A0 03 00 21 00 00 D9 2D 03 2D = DE MUTE
                        // FF 55 08 2E A0 0A 00 31 00 00 D9 16 01 0D = A
                        // FF 55 08 2E A0 0B 00 11 00 00 D9 35 01 0D = B

                        switch (buffer[5]) {
                            case 0x01:
                                powerOn = true;
                                listener.onPowerChanged(true);
                                break;
                            case 0x02:
                                powerOn = false;
                                listener.onPowerChanged(false);
                                break;
                            case 0x03:
                                muted = buffer[6] == 0x01;
                                listener.onMuteChanged(muted);
                                break;
                            case 0x04:
                            case 0x05:
                                volume = buffer[6];
                                listener.onVolumeChanged(volume);
                                break;

                            case 0x06:
                                // Channel 1
                                source = 2;
                                listener.onSourceChanged(source);
                                break;
                            case 0x07:
                                // Channel 2
                                source = 3;
                                listener.onSourceChanged(source);
                                break;
                            case 0x08:
                                // Channel 3
                                source = 4;
                                listener.onSourceChanged(source);
                                break;
                            case 0x09:
                                // Channel 4
                                source = 5;
                                listener.onSourceChanged(source);
                                break;

                            case 0x0A:
                                // Zone A
                                break;
                            case 0x0B:
                                // Zone B
                                break;
                            case 0x0C:
                                // Zone C
                                break;
                        }
                    }
                }

            } catch (IOException e) {
                if(listener!=null)
                    listener.onDisconnect(socket, e.getMessage());
            }
        }
    }

    public void setClientCallback(ClientCallback listener){
        this.listener=listener;
    }

    public void removeClientCallback(){
        this.listener=null;
    }

    public interface ClientCallback {
        void onPowerChanged(Boolean poweredOn);
        void onMuteChanged(Boolean muted);
        void onVolumeChanged(int level);
        void onSourceChanged(int level);
        void onConnect(Socket socket);
        void onDisconnect(Socket socket, String message);
        void onConnectError(Socket socket, String message);
    }
}