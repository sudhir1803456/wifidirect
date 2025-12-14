package com.wifidirect.app;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;
import android.net.wifi.p2p.WifiP2pDeviceList;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pConfig;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.net.wifi.WifiManager;

public class WifiDirectHandler {

    private PeerChangeCallback peerChangeCb;
    private Context context;
    private final WifiManager wifiManager;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel p2pChannel;
    private WifiP2pManager.ChannelListener p2pChannelListener;
    private WifiP2pManager.ActionListener p2pActionListner;
    private WifiP2pManager.ActionListener p2pConnectActionListner;
    private WiFiDirectBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    boolean isP2PSupported = true;

    public WifiDirectHandler(Context context, PeerChangeCallback cb) {
        this.context = context;
        this.peerChangeCb = cb;

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) 
        {
            Log.d(LogTags.AWARE_ASM, "setWifiEnabled is true");
            wifiManager.setWifiEnabled(true);
        }
        else
        {
            Log.d(LogTags.AWARE_ASM, "isWifiEnabled is enabled");
        }
        if (manager == null) {
            isP2PSupported = false;
            Log.d(LogTags.AWARE_ASM, "WiFi Direct NOT supported");
            return;
        }

        Log.d(LogTags.AWARE_ASM, "WiFi Direct supported");

        // CHANNEL LISTENER
        p2pChannelListener = () -> {
            Log.d(LogTags.AWARE_ASM, "P2P channel disconnected");
        };

        // ACTION LISTENER
        p2pActionListner = new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reason) {
                Log.d(LogTags.AWARE_ASM, "WiFi Direct scanning FAILED: " + reason);
                // peerChangeCb.OnPeerChange("Scan failed: " + reason);
            }

            @Override
            public void onSuccess() {
                Log.d(LogTags.AWARE_ASM, "WiFi Direct scanning SUCCESS");
                // peerChangeCb.OnPeerChange("Scan success");
            }
        };
        // connect ACTION LISTENER
        p2pConnectActionListner = new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reason) {
                Log.d(LogTags.AWARE_ASM, "connect failed: " + reason);
                peerChangeCb.showToastPopup("connect failed!");
            }

            @Override
            public void onSuccess() {
                Log.d(LogTags.AWARE_ASM, "connect SUCCESS");
                peerChangeCb.showToastPopup("connect SUCCESS");
            }
        };

        // INIT CHANNEL
        p2pChannel = manager.initialize(context, Looper.getMainLooper(), p2pChannelListener);


        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        receiver = new WiFiDirectBroadcastReceiver(manager, p2pChannel, peerChangeCb);

    }
    public void register() {
        Log.d(LogTags.AWARE_ASM, "register");
        context.registerReceiver(receiver, intentFilter);
        // START SCANNING
        manager.discoverPeers(p2pChannel, p2pActionListner);
    }
    public void startdiscovery()
    {
        Log.d(LogTags.AWARE_ASM, "startdiscovery");
        manager.discoverPeers(p2pChannel, p2pActionListner);
    }
    public void unregister() {
        Log.d(LogTags.AWARE_ASM, "unregister");

        context.unregisterReceiver(receiver);
    }

    public boolean P2PSupported() {
        return isP2PSupported;
    }

    public void connectToDevice(String deviceMAC) {
        Log.d(LogTags.AWARE_ASM, "connecting to : "+deviceMAC);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceMAC;
        manager.connect(p2pChannel,config,p2pConnectActionListner);
    }
}

