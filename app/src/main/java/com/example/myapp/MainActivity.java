package com.wifidirect.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements PeerChangeCallback {
    private MyCustomForm form;
    private WifiDirectHandler wifidirect;
    TextView peerDevice;
    LinearLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        parentLayout= findViewById(R.id.mainLinearLayout); // the root LinearLayout

        TextView text = findViewById(R.id.wifidirectTest);
        peerDevice = findViewById(R.id.DeviceList);
        Log.d(LogTags.AWARE_ASM, "MainActivity onCreate started");

        wifidirect = new WifiDirectHandler(this, this);

        AwareHandler aware  = new AwareHandler(this);
        form = new MyCustomForm(this);
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            Log.d(LogTags.AWARE_ASM,"refreshbutton clicked, starting discovery again");
            form.clearDeviceListOnly(parentLayout);
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

    @Override
    protected void onPause() {
        Log.d(LogTags.AWARE_ASM, "MainActivity UI onPause ");
        super.onPause();
        wifidirect.unregister();
    }   
    // CALLBACK IMPLEMENTATION
    @Override
    public void onResult(Collection<WifiP2pDevice>p2pDevices) {
        form.clearDeviceListOnly(parentLayout);

        String peerInfo = "";
        if (p2pDevices.isEmpty())
        {
            peerInfo = "No P2P device Available";
            form.AddDeviceNameAndMACIntoList("No P2P devices","No P2P MAC Available",parentLayout);
            return;
        }
        for (WifiP2pDevice dev : p2pDevices) {
            Log.d(LogTags.AWARE_ASM, "P2P Device and P2P MAC" + dev.deviceName + ": "+dev.deviceAddress);
            peerInfo = dev.deviceName +" and "+dev.deviceAddress;
            form.showToast("P2P search devices: "+dev.deviceName +" "+dev.deviceAddress);
            form.AddDeviceNameAndMACIntoList(dev.deviceName,dev.deviceAddress,parentLayout);
        }
        Log.d(LogTags.AWARE_ASM, "p2pDevices changes");
    }
}
