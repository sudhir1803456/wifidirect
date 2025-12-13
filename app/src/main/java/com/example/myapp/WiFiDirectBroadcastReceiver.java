package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.Collection;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi P2P events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private PeerChangeCallback peerChangeCb;

    // PEER LIST LISTENER
    private final WifiP2pManager.PeerListListener p2pPeerListner =
        new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.d("P2P", "Peers found: " + peers.getDeviceList().size());
                Collection<WifiP2pDevice> p2pDevices = peers.getDeviceList();

                if (p2pDevices.isEmpty())
                {
                    peerChangeCb.onResult("No P2P devices","No P2P MAC Available");
                    return;
                }

                for (WifiP2pDevice dev : p2pDevices) {
                    Log.d(LogTags.AWARE_ASM, "P2P Device: " + dev.deviceName);
                    Log.d(LogTags.AWARE_ASM, "P2P MAC: " + dev.deviceAddress);

                    peerChangeCb.onResult(dev.deviceName,dev.deviceAddress);
                }
            }
        };

    public WiFiDirectBroadcastReceiver(
            WifiP2pManager manager,
            WifiP2pManager.Channel channel,
            PeerChangeCallback cb
    ) {
        this.manager = manager;
        this.channel = channel;
        this.peerChangeCb = cb;

        Log.d(LogTags.AWARE_ASM, "WiFiDirectBroadcastReceiver created");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            int state = intent.getIntExtra(
                    WifiP2pManager.EXTRA_WIFI_STATE,
                    -1
            );

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(LogTags.AWARE_ASM, "WIFI P2P IS ENABLED");
            } else {
                Log.d(LogTags.AWARE_ASM, "WIFI P2P IS DISABLED");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            Log.d(LogTags.AWARE_ASM, "Calling requestPeers");

            if (manager != null && channel != null) {
                manager.requestPeers(channel, p2pPeerListner);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            Log.d(LogTags.AWARE_ASM, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
            // Respond to new connection or disconnection

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            Log.d(LogTags.AWARE_ASM, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            // Respond to this device's Wi-Fi state change
        }
    }
}
