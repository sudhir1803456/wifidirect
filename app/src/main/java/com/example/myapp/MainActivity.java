package com.wifidirect.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.InetSocketAddress;

public class MainActivity extends Activity implements PeerChangeCallback {
    private MyCustomForm form;
    private WifiDirectHandler wifidirect;
    TextView peerDevice;
    LinearLayout deviceListLayout;
    LinearLayout connectedDeviceLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        deviceListLayout= findViewById(R.id.deviceContainer); // the DeviceList LinearLayout
        connectedDeviceLayout = findViewById(R.id.connectedDevice);//container for connected device
        TextView text = findViewById(R.id.wifidirectTest);
        Log.d(LogTags.AWARE_ASM, "MainActivity onCreate started");

        wifidirect = new WifiDirectHandler(this, this);

        AwareHandler aware  = new AwareHandler(this);
        form = new MyCustomForm(this,this);
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            Log.d(LogTags.AWARE_ASM,"refreshbutton clicked, starting discovery again");
            deviceListLayout.removeAllViews();
            wifidirect.startdiscovery();
        });
        if (aware.AwareSupportedDevice()) {
            form.showToast("Your device supports Aware");
        } else {
            form.showToast("Your device does NOT support Aware");
        }

        Log.d(LogTags.AWARE_ASM, "MainActivity UI successfully created");
        
    }
    @Override
    protected void onResume() {
        Log.d(LogTags.AWARE_ASM, "MainActivity UI onResume ");
        super.onResume();
        wifidirect.register();
    }

    // @Override
    // protected void onPause() {
    //     Log.d(LogTags.AWARE_ASM, "MainActivity UI onPause ");
    //     super.onPause();
    //     wifidirect.unregister();
    // }   
    // CALLBACK IMPLEMENTATION
    @Override
    public void OnPeerChange(Collection<WifiP2pDevice>p2pDevices) {
        deviceListLayout.removeAllViews();
        if (p2pDevices.isEmpty())
        {
            form.AddDeviceNameAndMACIntoList("No P2P devices","No P2P MAC Available",deviceListLayout);
            return;
        }
        for (WifiP2pDevice dev : p2pDevices) {
            Log.d(LogTags.AWARE_ASM, "P2P Device and P2P MAC" + dev.deviceName + ": "+dev.deviceAddress);
            String peerInfo = dev.deviceName +" and "+dev.deviceAddress;
            // form.showToast("P2P search devices: "+dev.deviceName +" "+dev.deviceAddress);
            form.AddDeviceNameAndMACIntoList(dev.deviceName,dev.deviceAddress,deviceListLayout);
        }
        Log.d(LogTags.AWARE_ASM, "p2pDevices changes");
    }
    @Override
    public void ConnectToDevice(String deviceInfo)
    {
        form.showToast("connecting to " +deviceInfo);
        wifidirect.connectToDevice(deviceInfo);
    }
    @Override
    public void showToastPopup(String text)
    {
        form.showToast(text);
    }
    @Override
    public void ShowConnectedDevice(String deviceName)
    {
        form.AddDeviceToConnectedLayout(deviceName,connectedDeviceLayout);
    }
    @Override 
    public void OnConnected()
    {

    }
    @Override
    public void OnDisconnected()
    {
        connectedDeviceLayout.removeAllViews();
    }
    @Override
    public void DisconnectToDevice()
    {
        connectedDeviceLayout.removeAllViews();
        wifidirect.disconnectDevice();
    }

    @Override 
    public void CreateServerSocket()
    {
        Log.d(LogTags.AWARE_ASM,"CreateServerSocket");
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                Log.d(LogTags.AWARE_ASM, "ServerSocket created, waiting for client...");
                Socket client = serverSocket.accept();
                Log.d(LogTags.AWARE_ASM, "Client connected: " + client.getInetAddress());
                ((Activity) MainActivity.this).runOnUiThread(() -> {
                    form.showToast("client connected successfully!");
                });

            } catch (IOException e) {
                Log.d(LogTags.AWARE_ASM,"error occured "+e);
            }
        }).start();

    }

    @Override
    public void CreateSocketAtClient(String GOIp)
    {
        Log.d(LogTags.AWARE_ASM,"CreateSocketAtClient and added sleep of 3 second");
        new Thread(() -> {
            try {
                Log.d(LogTags.AWARE_ASM,"thread started..");
                Thread.sleep(3000); // wait for server
                Log.d(LogTags.AWARE_ASM,"thread sleep over started..");

                Socket socket = new Socket();
                Log.d(LogTags.AWARE_ASM,"new socket created..");
                Log.d(LogTags.AWARE_ASM, "Connecting to server at " + GOIp + ":8888");
                if(GOIp.isEmpty())
                {
                    Log.d(LogTags.AWARE_ASM,"GOIP is null..");
                    return;
                }
                socket.connect(new InetSocketAddress(GOIp, 8888), 5000); // 5s timeout
                Log.d(LogTags.AWARE_ASM, "Connected to server");
                ((Activity) MainActivity.this).runOnUiThread(() -> {
                    form.showToast("connected to server successfully!");
                });
            }
            catch (InterruptedException e)
            {
                Log.d(LogTags.AWARE_ASM,"error occured "+e);

            } 
            catch (IOException e) {
                Log.d(LogTags.AWARE_ASM,"error occured "+e);
            }
        }).start();
    }

}
