//package com.example.erik.dynaudioremote;
//
//import android.content.Context;
//import android.net.wifi.WifiManager;
//import android.support.constraint.ConstraintLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.SeekBar;
//import android.widget.ToggleButton;
//import android.support.design.widget.Snackbar;
//
//import java.math.BigInteger;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.Socket;
//
//import dymote.Dymote;
//
//public class Remote extends AppCompatActivity {
////    private CommandDispatcher commandDispatcher;
//    Client client = new Client("192.168.2.172", 1901);
//    ToggleButton[] sourceButtons = new ToggleButton[4];
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_remote);
//
//        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
//        final Snackbar snackbar = Snackbar
//                .make(constraintLayout, "No connection with Dynaudio Connect", Snackbar.LENGTH_INDEFINITE);
//
//        final Snackbar noWifiSnackbar = Snackbar
//                .make(constraintLayout, "No Wi-Fi connection", Snackbar.LENGTH_INDEFINITE);
//
//        Log.i("Dymote", "Before");
//        Dymote.connect("192.168.2.172", 1901);
//        Log.i("Dymote", "After");
//
////        Spinner spinner = (Spinner) findViewById(R.id.channelSelector);
////        // Create an ArrayAdapter using the string array and a default spinner layout
////        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
////                R.array.inputoptions_array, android.R.layout.simple_spinner_item);
////        // Specify the layout to use when the list of choices appears
////        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        // Apply the adapter to the spinner
////        spinner.setAdapter(adapter);
//
////
////        commandDispatcher = new CommandDispatcher();
////        commandDispatcher.start();
////
////
////        mServiceIntent.setAction("Init");
////
////        this.startService(mServiceIntent);
////
////        String messageStr="";
////        int server_port = 1900; //port that I’m using
////        try{
////            DatagramSocket s = new DatagramSocket();
////            InetAddress local = InetAddress.getByName("192.168.2.81");//my broadcast ip
////       //     byte[] message = {(byte)0xFF, 0x55, 0x05, 0x2F, (byte)0xA0, 0, 0, 0x31, (byte)0xFB};
////            byte[] message = {};
////            int msg_length=0;
////
////            WifiManager wifi;
////            wifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
////            WifiManager.MulticastLock mLock = wifi.createMulticastLock("lock");
////            mLock.acquire();
////
////            DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
////            s.send(p);
////            Log.d("rockman","message send " + local.toString());
////
////            mLock.release();
////
////            //Wait for a response
////            byte[] recvBuf = new byte[15000];
////            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
////            s.receive(receivePacket);
////
////            //We have a response
////            System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
////        }catch(Exception e){
////            Log.d("rockman","error  " + e.toString());
////        }
//
//
//
//
//        sourceButtons[0] = (ToggleButton) findViewById(R.id.btnSource1);
//        sourceButtons[1] = (ToggleButton) findViewById(R.id.btnSource2);
//        sourceButtons[2] = (ToggleButton) findViewById(R.id.btnSource3);
//        sourceButtons[3] = (ToggleButton) findViewById(R.id.btnSource4);
//
//        sourceButtons[0].setTag(2);
//        sourceButtons[1].setTag(3);
//        sourceButtons[2].setTag(4);
//        sourceButtons[3].setTag(5);
//
//        client.setClientCallback(new Client.ClientCallback () {
//            @Override
//            public void onPowerChanged(final Boolean poweredOn) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("onPowerChanged", Boolean.toString(poweredOn));
//
//                        ToggleButton powerButton = (ToggleButton) findViewById(R.id.btnPower);
//                        powerButton.setChecked(poweredOn);
//                    }
//                });
//            }
//
//            @Override
//            public void onMuteChanged(final Boolean muted) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("onMuteChanged", Boolean.toString(muted));
//
//                        ToggleButton muteButton = (ToggleButton) findViewById(R.id.btnMute);
//                        muteButton.setChecked(muted);
//                    }
//                });
//            }
//
//            @Override
//            public void onVolumeChanged(final int level) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("onVolumeChanged", Integer.toString(level));
//
//                        SeekBar volumeBar = (SeekBar) findViewById(R.id.volumeBar);
//                        volumeBar.setProgress(level);
//
//                        ImageButton btnVolDown = (ImageButton) findViewById(R.id.btnVolDown);
//                        btnVolDown.setEnabled(level > 0);
//
//                        ImageButton btnVolUp = (ImageButton) findViewById(R.id.btnVolUp);
//                        btnVolUp.setEnabled(level < 11);
//                    }
//                });
//            }
//
//            @Override
//            public void onSourceChanged(final int source) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("onSourceChanged", Integer.toString(source));
//
//                        for (ToggleButton sourceButton : sourceButtons) {
//                            int tag = (int) sourceButton.getTag();
//
//                            if (tag == source) {
//                                sourceButton.setChecked(true);
//                                sourceButton.setClickable(false);
//                            } else {
//                                sourceButton.setChecked(false);
//                                sourceButton.setClickable(true);
//                            }
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onConnect(Socket socket) {
//                Log.e("onConnect", "connected");
//                if (snackbar.isShown()) {
//                    snackbar.dismiss();
//                }
//            }
//
//            @Override
//            public void onDisconnect(Socket socket, final String message) {
//                snackbar.show();
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("onDisconnect", message);
//
//                      //  snackbar.show();
//                    }
//                });
//            }
//
//            @Override
//            public void onConnectError(Socket socket, String message) {
//                Log.e("onConnectError", message);
//            }
//        });
//
////        client.connect();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        client.disconnect();
//    }
//
//    public String toHex(String arg) {
//        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
//    }
//
//    public void mute(View view) {
//        doCommand(Command.MUTE);
//    }
//
//    public void volumeUp(View view) {
//        doCommand(Command.VOLUME_UP);
//    }
//
//    public void volumeDown(View view) {
//        doCommand(Command.VOLUME_DOWN);
//    }
//
//    public void togglePower(View view) {
//        ToggleButton powerButton = (ToggleButton) findViewById(R.id.btnPower);
//        if (powerButton.isChecked()) {
//            doCommand(Command.POWER_ON);
//
//            Log.i("Dymote", "Before");
//            Dymote.powerOn();
//            Log.i("Dymote", "After");
//
//        } else {
//            doCommand(Command.POWER_OFF);
//        }
//    }
//
//    public void changeSource(View view) {
//        int source = (int)view.getTag();
//
//        switch (source) {
//            case 2: {
//                doCommand(Command.SOURCE_LINE);
//                break;
//            }
//            case 3: {
//                doCommand(Command.SOURCE_OPTICAL);
//                break;
//            }
//            case 4: {
//                doCommand(Command.SOURCE_COAX);
//                break;
//            }
//            case 5: {
//                doCommand(Command.SOURCE_USB);
//                break;
//            }
//        }
//    }
//
//    private void doCommand(final Command cmd) {
////        commandDispatcher.execute(cmd);;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                client.send(cmd);
//            }
//        }).start();
//    }
//}
