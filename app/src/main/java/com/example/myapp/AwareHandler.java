package com.example.myapp;

import android.content.Context;
import android.net.wifi.aware.WifiAwareManager;
import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.WifiAwareSession;
import android.net.wifi.aware.PublishConfig;
import android.util.Log;

public class AwareHandler {

    private WifiAwareManager awareManager;
    private WifiAwareSession awareSession;
    boolean isDeviceSupportedAware = false;
    public AwareHandler(Context context)
    {
        awareManager = (WifiAwareManager) context.getSystemService(Context.WIFI_AWARE_SERVICE);
        if (awareManager == null) {
            Log.d(LogTags.AWARE_ASM, "WiFi Aware not supported on this device");
            return;
        }
        isDeviceSupportedAware = true;
        Log.d(LogTags.AWARE_ASM, "WiFi Aware supported on this device");


        awareManager.attach(new AttachCallback() {
            @Override
            public void onAttached(WifiAwareSession session) {
                awareSession = session;
                Log.d(LogTags.AWARE_ASM, "WiFi Aware attached");
            }

            @Override
            public void onAttachFailed() {
                Log.d(LogTags.AWARE_ASM, "WiFi Aware attach failed");
            }
        }, null);
    }
    public boolean AwareSupportedDevice()
    {
        return isDeviceSupportedAware;
    }

    
}
