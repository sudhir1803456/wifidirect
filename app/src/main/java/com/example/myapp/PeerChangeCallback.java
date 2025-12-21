package com.wifidirect.app;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.content.Intent;
import android.net.Uri;

public interface PeerChangeCallback {
    void onAllPermissionsGranted();
    void onPermissionNotGranted();
    void OnPeerChange(Collection<WifiP2pDevice>p2pDevices);
    void ConnectToDevice(String deviceInfo);
    void DisconnectPeerDevice();
    void ShowConnectedDevice(String deviceName);
    void showToastPopup(String text);
    void CreateServerSocket();
    void CreateSocketAtClient(String GOIp);
    void OnDisconnected();
    void OnConnected();
    void StartActivityForFile(Intent intent);
    void SendFileToServer(Uri fileUri);
}
