package com.dftreactnative;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dft.onyx.core;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by ericolszewski on 6/1/18.
 * Transmute
 */

public class VerifyTask  extends AsyncTask<VerifyPayload, Void, Float> {
    private Exception mException = null;
    private Context mContext = null;

    private OnVerifyTaskCompleted listener;

    public VerifyTask(Context context, OnVerifyTaskCompleted listener) {
        mContext = context;
        this.listener = listener;
    }

    public interface OnVerifyTaskCompleted {
        void onVerifyTaskCompleted(String result, Float matchScore);
    }

    @Override
    protected Float doInBackground(VerifyPayload... payloads) {
        try {
            Mat probe = new Mat();
            Utils.bitmapToMat(payloads[0].getProbe(), probe);

            Imgproc.cvtColor(probe, probe, Imgproc.COLOR_RGB2GRAY);
            return core.pyramidVerify(payloads[0].getReference(), probe);
        } catch (Exception e) {
            mException = e;
        }
        return -1.0f;
    }

    @Override
    protected void onPostExecute(Float matchScore) {
        listener.onVerifyTaskCompleted(createMatchString(matchScore), matchScore);
    }

    private String createMatchString(double score) {
        String matchString;
        if (score < 0.1) {
            matchString = new String("Failed");
            OnyxModule.triggerAlert("Match failed");
        } else {
            matchString = new String("Match");
            OnyxModule.triggerAlert("Match success");
        }
        matchString += " (Score = " + String.format("%.2f", score) + ")";

        return matchString;
    }
}