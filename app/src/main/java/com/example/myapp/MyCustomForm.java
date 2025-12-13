package com.wifidirect.app;

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

    // // ----- First input box -----
    // mainInput = new EditText(context);
    // mainInput.setHint("Enter MAC Address");
    // mainInput.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
    // mainInput.setBackgroundResource(android.R.drawable.editbox_background);
    // LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
    //         dpToPx(300),
    //         ViewGroup.LayoutParams.WRAP_CONTENT);
    // inputParams.gravity = Gravity.CENTER_HORIZONTAL;  // center it
    // inputParams.setMargins(0, dpToPx(16), 0, dpToPx(16));
    // mainInput.setLayoutParams(inputParams);
    // rootLayout.addView(mainInput);
// ----- Non-editable text item -----
    mainText = new TextView(context);
    // mainText.setText("No P2P Device Available");  // fixed text
    mainText.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
    mainText.setGravity(Gravity.CENTER);
    mainText.setTextSize(10);

    // make it look clickable
    mainText.setClickable(true);
    mainText.setBackgroundResource(android.R.drawable.btn_default); // gives button look

    // layout params
    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
            dpToPx(300),
            ViewGroup.LayoutParams.WRAP_CONTENT
    );
    // textParams.gravity = Gravity.CENTER_HORIZONTAL;
    textParams.setMargins(0, dpToPx(16), 0, dpToPx(16));

    mainText.setLayoutParams(textParams);
    rootLayout.addView(mainText);

    // click event
    mainText.setOnClickListener(v -> {
        Toast.makeText(context, "clicked ON ", Toast.LENGTH_SHORT).show();
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
    public void AddDeviceNameAndMACIntoList(String deviceName, String deviceMAC)
    {
        ((Activity) context).runOnUiThread(() -> {
            String oldText = mainText.getText().toString();
            String newText = deviceName + " : " +deviceMAC;
            String updatedText = oldText.isEmpty()? newText : oldText +"\n"+newText;
            mainText.setText(deviceName + " and " +deviceMAC); // fixed text
        });
    }
}
