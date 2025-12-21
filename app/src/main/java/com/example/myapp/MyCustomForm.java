package com.wifidirect.app;
import android.util.TypedValue;
import android.graphics.Color;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class MyCustomForm {
    private Context context;
    private PeerChangeCallback peerChangeCb;

    public MyCustomForm(Context context,PeerChangeCallback cb) {
        this.peerChangeCb = cb;
        this.context = context;
        Log.d(LogTags.AWARE_ASM, "MyCustomForm: constructor");
    }


    // Utility: show toast
    public void showToast(String msg) {
        ((Activity) context).runOnUiThread(() ->{
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
        Log.d(LogTags.AWARE_ASM, "MyCustomForm: Toast shown -> " + msg);
    }

    // Helper to convert dp to px
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private TextView createTextView(String text)
    {

        TextView tv = new TextView(context);

        // Text
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

        // Padding
        int pad = dpToPx(12);
        tv.setPadding(pad, pad, pad, pad);

        // Make it clickable
        tv.setClickable(true);
        tv.setFocusable(true);

        // Ripple background (modern Android)
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(
                android.R.attr.selectableItemBackground,
                outValue,
                true
        );
        tv.setBackgroundResource(outValue.resourceId);

        // Layout params (responsive width)
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        params.setMargins(0, dpToPx(8), 0, dpToPx(8));
        tv.setLayoutParams(params);

        tv.setOnClickListener(v -> {
            String[] parts = text.split("-");
            String deviceMAC = "";
            if (parts.length > 1) {
                deviceMAC = parts[1];
            }
            Log.d(LogTags.AWARE_ASM, "Device MAC is->"+deviceMAC);
            if(!deviceMAC.isEmpty())
            {
                peerChangeCb.ConnectToDevice(deviceMAC);
            }
            else{
                peerChangeCb.DisconnectPeerDevice();
            }
        });


        return tv;
    }

    public void AddDeviceNameAndMACIntoList(String deviceName, String deviceMAC, LinearLayout rootLayout)
    {
        ((Activity) context).runOnUiThread(() -> {
            String peerInfo = deviceName + "-" +deviceMAC;
            TextView peerView = createTextView(peerInfo);
                rootLayout.addView(peerView);
        });
    }
    public void AddDeviceToConnectedLayout(String deviceName, LinearLayout rootLayout)
    {
        ((Activity) context).runOnUiThread(() -> {
            TextView peerView = createTextView("connected to "+deviceName+"(tap to disconnect)");
                rootLayout.addView(peerView);
        });
    }
    public void AddFileButton(LinearLayout fileLayout)
    {
        Log.d(LogTags.AWARE_ASM,"creating file button");
        // Create button dynamically
        Button selectFileButton = new Button(context);
        selectFileButton.setText("Select File");
       // Create layout params with WRAP_CONTENT width & height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Set gravity to center horizontally
        params.gravity = Gravity.CENTER_HORIZONTAL;
        // Apply the layout params to the button
        selectFileButton.setLayoutParams(params);
        selectFileButton.setPadding(16, 16, 16, 16);
        // Add click listener
        selectFileButton.setOnClickListener(v -> {
            // Open file picker
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // all file types
            peerChangeCb.StartActivityForFile(intent);
        });
        fileLayout.addView(selectFileButton);
    }
    public void AddSendButton(LinearLayout fileLayout,Uri fileUri)
    {
        Log.d(LogTags.AWARE_ASM,"creating file button");
        // Create button dynamically
        Button sendButton = new Button(context);
        sendButton.setText("Send");
       // Create layout params with WRAP_CONTENT width & height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Set gravity to center horizontally
        params.gravity = Gravity.CENTER_HORIZONTAL;
        // Apply the layout params to the button
        sendButton.setLayoutParams(params);
        sendButton.setPadding(16, 16, 16, 16);
        // Add click listener
        sendButton.setOnClickListener(v -> {
            new Thread(() -> {
                peerChangeCb.SendFileToServer(fileUri);
            }).start();
        });
        fileLayout.addView(sendButton);
    }

}
