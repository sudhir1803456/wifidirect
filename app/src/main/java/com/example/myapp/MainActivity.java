package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements PeerChangeCallback {
    private MyCustomForm form;
    private WifiDirectHandler wifidirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LogTags.AWARE_ASM, "MainActivity onCreate started");

        // PASS CALLBACK TO WifiDirectHandler
        wifidirect = new WifiDirectHandler(this, this);

        AwareHandler aware  = new AwareHandler(this);
        form = new MyCustomForm(this);
        setContentView(form.getView());
        if (aware.AwareSupportedDevice()) {
            form.showToast("Your device supports Aware");
        } else {
            form.showToast("Your device does NOT support Aware");
        }

        // form.setOnOkClicked(input -> {
        //     Log.d(LogTags.AWARE_ASM, "OK clicked with input: " + input);
        //     form.showToast("You entered: " + input);
        // });

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
    public void onResult(String name, String MAC) {
        Log.d(LogTags.AWARE_ASM, "Callback from WifiDirectHandler: " + name + " and "+ MAC);
        form.showToast("P2P search devices: "+name +" "+MAC);
        form.AddDeviceNameAndMACIntoList(name,MAC);
    }
}
