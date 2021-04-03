package com.dftreactnative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.core;
import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxConfigurationBuilder;
import com.dft.onyxcamera.config.OnyxError;
import com.dft.onyxcamera.config.OnyxResult;
import com.dft.onyxcamera.ui.reticles.Reticle;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import static com.dftreactnative.ValuesUtil.*;

/**
 * Created by cdwheatley on 4/2/21.
 * Updated for latest Onyx configuration from Onyx Android Demo project
 */

public class OnyxSetupActivity extends AppCompatActivity {
    private final static String TAG = OnyxSetupActivity.class.getSimpleName();
    private static final int ONYX_REQUEST_CODE = 1337;
    MainApplication application = new MainApplication();
    OnyxConfiguration.SuccessCallback successCallback;
    OnyxConfiguration.ErrorCallback errorCallback;
    OnyxConfiguration.OnyxCallback onyxCallback;
    private Activity activity;
    private Button startOnyxButton;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Unable to load OpenCV!");
        } else {
            Log.i(TAG, "OpenCV loaded successfully");
            core.initOnyx();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setupUI();
        setupCallbacks();
    }

    private void setupCallbacks() {
        successCallback = new OnyxConfiguration.SuccessCallback() {
            @Override
            public void onSuccess(OnyxResult onyxResult) {
                MainApplication.setOnyxResult(onyxResult);
                displayResults(onyxResult);
            }
        };

        errorCallback = new OnyxConfiguration.ErrorCallback() {
            @Override
            public void onError(OnyxError onyxError) {
                application.setOnyxError(onyxError);
            }
        };

        onyxCallback = new OnyxConfiguration.OnyxCallback() {
            @Override
            public void onConfigured(Onyx configuredOnyx) {
                application.setConfiguredOnyx(configuredOnyx);
                startActivity(new Intent(activity, OnyxActivity.class));
            }
        };
    }

    @Override
    /**
     * This method handles the Android System onResume() callback.
     */
    public void onResume() {
        super.onResume();
        if (MainApplication.getOnyxResult() != null) {
            displayResults(MainApplication.getOnyxResult());
        }
    }

    private void displayResults(OnyxResult onyxResult) {
        startActivity(new Intent(this, OnyxImageryActivity.class));
    }

    private void setupOnyx(final Activity activity) {
        // Create an OnyxConfigurationBuilder and configure it with desired options
        OnyxConfigurationBuilder onyxConfigurationBuilder = new OnyxConfigurationBuilder()
                .setActivity(activity)
                .setLicenseKey(getResources().getString(R.string.license_key_value))
                .setReturnRawImage(getReturnRawImage(this))
                .setReturnProcessedImage(getReturnProcessedImage(this))
                .setReturnEnhancedImage(getReturnEnhancedImage(this))
                .setReturnWSQ(getReturnWSQ(this))
                .setReturnFingerprintTemplate(getReturnFingerprintTemplate(this))
                .setShowLoadingSpinner(getShowLoadingSpinner(this))
                .setUseOnyxLive(getUseOnyxLive(this))
                .setUseFlash(getUseFlash(this))
                .setImageRotation(getImageRotation(this))
                .setReticleOrientation(getReticleOrientation(this))
                .setCropSize(getCropSizeWidth(this), getCropSizeHeight(this))
                .setCropFactor(getCropFactor(this))
                .setSuccessCallback(successCallback)
                .setErrorCallback(errorCallback)
                .setOnyxCallback(onyxCallback);


        // Finally, build the OnyxConfiguration
        onyxConfigurationBuilder.buildOnyxConfiguration();
    }

    private void setupUI() {
        activity = this;
        setContentView(R.layout.activity_main);
        startOnyxButton = findViewById(R.id.start_onyx);
        startOnyxButton.bringToFront();
        startOnyxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.setOnyxResult(null);
                setupOnyx(activity);
            }
        });
    }
}