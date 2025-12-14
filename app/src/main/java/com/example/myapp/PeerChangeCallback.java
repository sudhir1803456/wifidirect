package com.wifidirect.app;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;

public interface PeerChangeCallback {
    void OnPeerChange(Collection<WifiP2pDevice>p2pDevices);
    void ConnectToDevice(String deviceInfo);
    void showToastPopup(String text);
}
