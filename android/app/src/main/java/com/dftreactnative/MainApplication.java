package com.dftreactnative;

import android.app.Activity;
import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.dft.onyxcamera.config.Onyx;
import com.dft.onyxcamera.config.OnyxConfiguration;
import com.dft.onyxcamera.config.OnyxError;
import com.dft.onyxcamera.config.OnyxResult;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends MultiDexApplication implements ReactApplication {
    private static OnyxConfiguration.SuccessCallback successCallback;
    private static OnyxConfiguration.ErrorCallback errorCallback;
    private static OnyxConfiguration.OnyxCallback onyxCallback;
    private static Onyx configuredOnyx;
    private static OnyxResult onyxResult;
    private static OnyxError onyxError;
    private static Activity activityForRunningOnyx;

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new OnyxPackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);
    }

    public static void setActivityForRunningOnyx(OnyxActivity activityForRunningOnyx) {
        MainApplication.activityForRunningOnyx = activityForRunningOnyx;
    }

    public static Activity getActivityForRunningOnyx() {
        return activityForRunningOnyx;
    }

    public void setSuccessCallback(OnyxConfiguration.SuccessCallback successCallback) {
        this.successCallback = successCallback;
    }

    public static OnyxConfiguration.SuccessCallback getSuccessCallback() {
        return successCallback;
    }

    public void setErrorCallback(OnyxConfiguration.ErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

    public static OnyxConfiguration.ErrorCallback getErrorCallback() {
        return errorCallback;
    }

    public void setOnyxCallback(OnyxConfiguration.OnyxCallback onyxCallback) {
        this.onyxCallback = onyxCallback;
    }

    public OnyxConfiguration.OnyxCallback getOnyxCallback() {
        return onyxCallback;
    }

    public void setConfiguredOnyx(Onyx configuredOnyx) {
        this.configuredOnyx = configuredOnyx;
    }

    public static Onyx getConfiguredOnyx() {
        return configuredOnyx;
    }

    public static void setOnyxResult(OnyxResult onyxResult) { MainApplication.onyxResult = onyxResult; }

    public static OnyxResult getOnyxResult() { return onyxResult; }

    public void setOnyxError(OnyxError onyxError) { this.onyxError = onyxError; }

    public static OnyxError getOnyxError() { return onyxError; }
}
