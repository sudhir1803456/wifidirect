package com.wifidirect.app;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;

public interface PeerChangeCallback {
    void onResult(Collection<WifiP2pDevice>p2pDevices);
}
