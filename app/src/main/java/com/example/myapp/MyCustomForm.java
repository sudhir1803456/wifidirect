package com.wifidirect.app;
import android.util.TypedValue;
import android.graphics.Color;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
public class MyCustomForm {

    private Context context;

    // private EditText mainInput;
    private TextView mainText;
    private EditText scrollInput;
    private Button okButton;
    private Spinner modeSpinner;

    private LinearLayout rootLayout;

    private String selectedMode = "Publish"; // default

    public MyCustomForm(Context context) {
        this.context = context;
        Log.d(LogTags.AWARE_ASM, "MyCustomForm: constructor");
        createUi();
    }

private void createUi()
{
    Log.d(LogTags.AWARE_ASM, "MyCustomForm: creating UI");

    rootLayout = new LinearLayout(context);
    rootLayout.setOrientation(LinearLayout.VERTICAL);
    int pad = dpToPx(16);
    rootLayout.setPadding(pad, pad, pad, pad);
    rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));

// ----- First input box -----
    TextView mainInput = new TextView(context);
    mainInput.setHint("###### P2P Available devices ######");
    mainInput.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
    mainInput.setBackgroundResource(android.R.drawable.editbox_background);
    LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
            dpToPx(300),
            ViewGroup.LayoutParams.WRAP_CONTENT);
    inputParams.gravity = Gravity.CENTER_HORIZONTAL;  // center it
    inputParams.setMargins(0, dpToPx(16), 0, dpToPx(16));
    mainInput.setLayoutParams(inputParams);
    rootLayout.addView(mainInput);

    // ----- Spinner (dropdown) for Publish / Subscribe -----
    modeSpinner = new Spinner(context);
    List<String> modes = new ArrayList<>();
    modes.add("Publisher");
    modes.add("Subscriber");

    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
            android.R.layout.simple_spinner_item, modes);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    modeSpinner.setAdapter(adapter);

    // UI improvements
    modeSpinner.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
    modeSpinner.setBackgroundResource(android.R.drawable.editbox_background);

    // Center alignment + margins
    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
            dpToPx(300),
            ViewGroup.LayoutParams.WRAP_CONTENT
    );
    spinnerParams.gravity = Gravity.CENTER_HORIZONTAL;
    spinnerParams.setMargins(0, dpToPx(16), 0, dpToPx(16));

    modeSpinner.setLayoutParams(spinnerParams);


    // rootLayout.addView(modeSpinner);

    // Spinner selection listener
    modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selectedMode = (String) parent.getItemAtPosition(pos);
            Log.d(LogTags.AWARE_ASM, "MyCustomForm: spinner selected -> " + selectedMode);
            if (modeChangeListener != null) {
                modeChangeListener.onModeChanged(selectedMode);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // no-op
        }
    });

    // // ----- OK Button -----
    // okButton = new Button(context);
    // okButton.setText("OK");
    // LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
    //         ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    // btnParams.gravity = Gravity.CENTER_HORIZONTAL;
    // okButton.setLayoutParams(btnParams);
    // rootLayout.addView(okButton);

    // // OK click handler
    // okButton.setOnClickListener(v -> {
    //     String input = mainInput.getText().toString();
    //     Log.d(LogTags.AWARE_ASM, "MyCustomForm: OK clicked. selectedMode=" + selectedMode + ", input=" + input);
    //     if (okClickListener != null) {
    //         okClickListener.onOkClicked(input);
    //     }
    // });

    Log.d(LogTags.AWARE_ASM, "MyCustomForm: UI created");
}

    public View getView() {
        Log.d(LogTags.AWARE_ASM, "MyCustomForm: getView()");
        return rootLayout;
    }

    // Expose currently selected mode
    public String getSelectedMode() {
        return selectedMode;
    }

    // Allow activity to set OK click handler
    private OnOkClickListener okClickListener;
    public void setOnOkClicked(OnOkClickListener listener)
    {
        Log.d(LogTags.AWARE_ASM, "OnOkClickListener: setOnOkClicked ");
        this.okClickListener = listener;
    }

    public interface OnOkClickListener {
        void onOkClicked(String inputText);
    }

    // Allow activity to listen for mode changes
    private OnModeChangeListener modeChangeListener;
    public void setOnModeChanged(OnModeChangeListener listener) {
        this.modeChangeListener = listener;
    }

    public interface OnModeChangeListener {
        void onModeChanged(String newMode);
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
        tv.setGravity(Gravity.CENTER_VERTICAL);

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

        // Click action
        tv.setOnClickListener(v ->
                Toast.makeText(context, "Clicked: " + text, Toast.LENGTH_SHORT).show()
        );

        return tv;
}

    public void AddDeviceNameAndMACIntoList(String deviceName, String deviceMAC)
    {
        ((Activity) context).runOnUiThread(() -> {
            String peerInfo = deviceName + " : " +deviceMAC;
            TextView peerView = createPeerTextView(peerInfo);
            rootLayout.addView(peerView);
        });
    }
    public void removeAllViews()
    {
        rootLayout.removeAllViews(); // clear old peers
    }
}
