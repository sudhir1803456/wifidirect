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

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
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
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.d(LogTags.AWARE_ASM, "MyCustomForm: Toast shown -> " + msg);
    }

    // Helper to convert dp to px
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    private TextView createPeerTextView(String text)
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
            peerChangeCb.ConnectToDevice(text);
            showToast("Connecting to " + text);
        });


        return tv;
    }

    public void AddDeviceNameAndMACIntoList(String deviceName, String deviceMAC, LinearLayout rootLayout)
    {
        ((Activity) context).runOnUiThread(() -> {
            String peerInfo = deviceName + " : " +deviceMAC;
            TextView peerView = createPeerTextView(peerInfo);
            rootLayout.addView(peerView);
        });
    }
    public void clearDeviceListOnly(LinearLayout rootLayout) {
        int childCount = rootLayout.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View v = rootLayout.getChildAt(i);
            if (v.getId() != R.id.wifidirectTest && v.getId() != R.id.refreshButton) {
                rootLayout.removeViewAt(i);
            }
        }
    }

}
