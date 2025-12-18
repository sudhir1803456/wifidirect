package com.wifidirect.app;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;

public interface PeerChangeCallback {
    void OnPeerChange(Collection<WifiP2pDevice>p2pDevices);
    void ConnectToDevice(String deviceInfo);
    void DisconnectToDevice();
    void ShowConnectedDevice(String deviceName);
    void showToastPopup(String text);
    void CreateServerSocket();
    void CreateSocketAtClient(String GOIp);
    void OnDisconnected();
    void OnConnected();
}
