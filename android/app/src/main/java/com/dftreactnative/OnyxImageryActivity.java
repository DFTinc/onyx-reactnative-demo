package com.dftreactnative;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dft.onyx.FingerprintTemplate;
import com.dft.onyx.NfiqMetrics;
import com.dft.onyxcamera.config.OnyxResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnyxImageryActivity extends Activity implements VerifyTask.OnVerifyTaskCompleted {
    private static final String TAG = com.dftreactnative.OnyxImageryActivity.class.getName();
    private final static String ENROLL_FILENAME = "enrolled_template.bin";

    private Activity activity;

    private FingerprintTemplate mCurrentTemplate = null;
    private FingerprintTemplate mEnrolledTemplate = null;

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_imagery);
        activity = this;
        ImageView rawImage1 = findViewById(R.id.rawImage1);
        ImageView processedImage1 = findViewById(R.id.processedImage1);
        ImageView rawImage2 = findViewById(R.id.rawImage2);
        ImageView processedImage2 = findViewById(R.id.processedImage2);
        ImageView rawImage3 = findViewById(R.id.rawImage3);
        ImageView processedImage3 = findViewById(R.id.processedImage3);
        ImageView rawImage4 = findViewById(R.id.rawImage4);
        ImageView processedImage4 = findViewById(R.id.processedImage4);
        TextView livenessTextView = findViewById(R.id.livenessText);
        TextView nfiqTextView1 = findViewById(R.id.nfiqText1);
        TextView nfiqTextView2 = findViewById(R.id.nfiqText2);
        TextView nfiqTextView3 = findViewById(R.id.nfiqText3);
        TextView nfiqTextView4 = findViewById(R.id.nfiqText4);
        rawImage1.setImageDrawable(null);
        processedImage1.setImageDrawable(null);
        rawImage2.setImageDrawable(null);
        processedImage2.setImageDrawable(null);
        rawImage3.setImageDrawable(null);
        processedImage3.setImageDrawable(null);
        rawImage4.setImageDrawable(null);
        processedImage4.setImageDrawable(null);

        OnyxResult onyxResult = MainApplication.getOnyxResult();
        if (onyxResult == null) {
            return;
        }

        ArrayList<Bitmap> rawImages = onyxResult.getRawFingerprintImages();
        ArrayList<Bitmap> processedImages = onyxResult.getProcessedFingerprintImages();
        ArrayList<TextView> nfiqTextViews = new ArrayList<>(Arrays.asList(
                nfiqTextView1,
                nfiqTextView2,
                nfiqTextView3,
                nfiqTextView4
        ));
        ArrayList<ImageView> rawImageViews = new ArrayList<>(Arrays.asList(
                rawImage1,
                rawImage2,
                rawImage3,
                rawImage4
        ));
        ArrayList<ImageView> processedImageViews = new ArrayList<>(Arrays.asList(
                processedImage1,
                processedImage2,
                processedImage3,
                processedImage4
        ));
        livenessTextView.setText(String.format("Liveness: %.2f", onyxResult.getMetrics().getLivenessConfidence()));
        if (onyxResult.getMetrics() != null && onyxResult.getMetrics().getNfiqMetrics() != null) {
            List<NfiqMetrics> nfiqMetricsList = onyxResult.getMetrics().getNfiqMetrics();
            for (int i = 0; i < nfiqMetricsList.size(); i++) {
                nfiqTextViews.get(i).setText(nfiqMetricsList.get(i) == null ? "" : "NFIQ: " + String.valueOf(nfiqMetricsList.get(i).getNfiqScore()));
            }
        }
        if (rawImages != null && processedImages != null) {
            for (int i = 0; i < rawImages.size(); i++) {
                if (rawImages.get(i) != null) {
                    rawImageViews.get(i).setImageBitmap(rawImages.get(i));
                }

                if (processedImages.get(i) != null) {
                    processedImageViews.get(i).setImageBitmap(processedImages.get(i));
                }
            }
        }

        Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.setOnyxResult(null);
                startActivity(new Intent(activity, OnyxSetupActivity.class));
            }
        });

        loadEnrolledTemplateIfExists(onyxResult);
    }

    /**
     * This method loads the fingerprint template if it exists.
     */
    private void loadEnrolledTemplateIfExists(OnyxResult onyxResult) {
        File enrolledFile = getFileStreamPath(ENROLL_FILENAME);
        if (enrolledFile.exists()) {
            try {
                FileInputStream enrollStream = openFileInput(ENROLL_FILENAME);
                ObjectInputStream ois = new ObjectInputStream(enrollStream);
                mEnrolledTemplate = (FingerprintTemplate) ois.readObject();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (StreamCorruptedException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        if (mEnrolledTemplate != null && onyxResult.getFingerprintTemplates().size() != 0) {
            VerifyTask verifyTask = new VerifyTask(getApplicationContext(), this);
            verifyTask.execute(new VerifyPayload(mEnrolledTemplate, onyxResult.getProcessedFingerprintImages().get(0)));
        } else if (onyxResult.getFingerprintTemplates().size() != 0) {
            mCurrentTemplate = onyxResult.getFingerprintTemplates().get(0);
            enrollCurrentTemplate();
        }
    }

    @Override
    public void onVerifyTaskCompleted(String result, Float matchScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setTitle("Match Result: " + result + " score = " + matchScore);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void enrollCurrentTemplate() {
        mEnrolledTemplate = mCurrentTemplate;
        deleteEnrolledTemplateIfExists();

        try {
            FileOutputStream enrollStream = this.openFileOutput(ENROLL_FILENAME, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(enrollStream);
            oos.writeObject(mEnrolledTemplate);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void deleteEnrolledTemplateIfExists() {
        File enrolledFile = getFileStreamPath(ENROLL_FILENAME);
        if (enrolledFile.exists()) {
            enrolledFile.delete();
        }
    }

}
