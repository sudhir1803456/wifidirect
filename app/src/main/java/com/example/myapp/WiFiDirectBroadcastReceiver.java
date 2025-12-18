package com.wifidirect.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.NetworkInfo;
import java.net.InetAddress;
import android.net.wifi.p2p.WifiP2pGroup;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi P2P events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private PeerChangeCallback peerChangeCb;
    private boolean isThisDeviceGO = false;
    // PEER LIST LISTENER
    private final WifiP2pManager.PeerListListener p2pPeerListner =
        new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.d(LogTags.AWARE_ASM, "Peers found: " + peers.getDeviceList().size());
                Collection<WifiP2pDevice> p2pDevices = peers.getDeviceList();
                peerChangeCb.OnPeerChange(p2pDevices);
            }
        };
    private void handleGroupInfo(WifiP2pGroup group) {
        if (group == null) return;

        // ----- GROUP OWNER -----
        WifiP2pDevice owner = group.getOwner();

        Log.d(LogTags.AWARE_ASM, "GO Name: " + owner.deviceName);
        Log.d(LogTags.AWARE_ASM, "GO MAC : " + owner.deviceAddress);
        if(!isThisDeviceGO)
        {
            peerChangeCb.ShowConnectedDevice(owner.deviceName);
        }
        
        // ----- CONNECTED CLIENTS -----
        Collection<WifiP2pDevice> clients = group.getClientList();
        int clientCount = (clients != null) ? clients.size() : 0;

        if (isThisDeviceGO && clientCount == 0) {
            peerChangeCb.OnDisconnected();
        }

        // ----- CONNECTED CLIENTS -----
        if (clients != null) {
            for (WifiP2pDevice client : clients) {
                Log.d(LogTags.AWARE_ASM, "Client Name: " + client.deviceName);
                Log.d(LogTags.AWARE_ASM, "Client MAC : " + client.deviceAddress);

                if (isThisDeviceGO) {
                    peerChangeCb.ShowConnectedDevice(client.deviceName);
                }
            }
        }

    }

    private final WifiP2pManager.ConnectionInfoListener connectionInfoListener =
        new WifiP2pManager.ConnectionInfoListener(){
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if (!info.groupFormed) return;

                    InetAddress goIp = info.groupOwnerAddress;

                    if (info.isGroupOwner) {
                        Log.d(LogTags.AWARE_ASM, "This device is GO");
                        isThisDeviceGO = true;
                        Log.d(LogTags.AWARE_ASM, "GO IP: " + goIp.getHostAddress());
                        peerChangeCb.CreateServerSocket();
                    } else {
                        Log.d(LogTags.AWARE_ASM, "This device is CLIENT");
                        Log.d(LogTags.AWARE_ASM, "GO IP: " + goIp.getHostAddress());
                        peerChangeCb.CreateSocketAtClient(goIp.getHostAddress());
                    }

                    // VERY IMPORTANT: request group details
                    manager.requestGroupInfo(channel, group -> handleGroupInfo(group));
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
            NetworkInfo networkInfo =
            intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo != null && networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, connectionInfoListener);
            }
            else if(networkInfo != null && !networkInfo.isConnected())
            {
                peerChangeCb.OnDisconnected();
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            Log.d(LogTags.AWARE_ASM, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}
