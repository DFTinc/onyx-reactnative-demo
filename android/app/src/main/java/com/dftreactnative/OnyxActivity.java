package com.dftreactnative;

import android.os.Bundle;

import com.dft.onyxcamera.config.Onyx;
import com.facebook.react.ReactActivity;

/**
 * Created by ericolszewski on 5/22/18.
 * Transmute
 */

public class OnyxActivity extends ReactActivity {
    private Onyx configuredOnyx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setting the activity being used to run Onyx here so that it can be finished from
        // the SuccessCallback in OnyxSetupActivity
        MainApplication.setActivityForRunningOnyx(this);

        // Get the configured Onyx that was returned from the OnyxCallback
        configuredOnyx = MainApplication.getConfiguredOnyx();

        // Creates Onyx in this activity
        configuredOnyx.create(this);

        // Make Onyx start the capture process
        // Important: configuredOnyx.capture() must occur after configuredOnyx.create() has been called
        if (!configuredOnyx.getOnyxConfig().isManualCapture()) {
            // Start the capture with auto capture process
            configuredOnyx.capture();
        }
    }
}