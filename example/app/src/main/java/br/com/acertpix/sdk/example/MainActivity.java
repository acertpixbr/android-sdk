package br.com.acertpix.sdk.example;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import br.com.acertpix.sdk.document_detector.Document;
import br.com.acertpix.sdk.document_detector.DocumentDetection;
import br.com.acertpix.sdk.document_detector.results.DocumentDetectionResult;
import br.com.acertpix.sdk.liveness_check.LivenessCheck;
import br.com.acertpix.sdk.liveness_check.results.LivenessCheckResult;


public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    private final static String ACCESS_TOKEN = "YOUR_ACERTPIX_ACCESS_TOKEN";

    LivenessCheck.Service livenessCheckService = LivenessCheck.Service.create(this);
    DocumentDetection.Service documentDetectionService = DocumentDetection.Service.create(this);

    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityResultRegistry();

        findViewById(R.id.button_liveness_demo).setOnClickListener(v -> {
            LivenessCheck liveness = new LivenessCheck.Builder(ACCESS_TOKEN)
                    .env(LivenessCheck.Env.PROD)
                    .build();

            livenessCheckService.start(liveness, this::handleLivenessCheckResult);
        });

        findViewById(R.id.button_document_detector_demo).setOnClickListener(v -> {
            DocumentDetection documentDetection = new DocumentDetection.Builder(ACCESS_TOKEN)
                    .steps(Arrays.asList(Document.RG_FRONT, Document.RG_BACK))
                    .env(DocumentDetection.Env.PROD)
                    .isPickFromLibraryEnabled(true)
                    .build();
            documentDetectionService.start(documentDetection, this::handleDocumentDetectionResult);
        });
    }

    protected void handleLivenessCheckResult(LivenessCheckResult sdkResult) {
        if (sdkResult == null) return;
        if (sdkResult.isSuccess()) {
            startSelfiePreview(sdkResult.getImageBase64());
            return;
        }
        if (sdkResult.getError() instanceof br.com.acertpix.sdk.liveness_check.results.error.InvalidTokenError) {
            // Invalid token
            Log.e(TAG, "Invalid token");
        } else {
            Snackbar.make(findViewById(android.R.id.content), sdkResult.getError().getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    protected void handleDocumentDetectionResult(DocumentDetectionResult sdkResult) {
        if (sdkResult == null) return;
        if (sdkResult.isSuccess()) {
            // Get document images iterating sdkResult.getResultSteps()
            return;
        }
        if (sdkResult.getError() instanceof br.com.acertpix.sdk.document_detector.results.error.InvalidTokenError) {
            // Invalid token
            Log.e(TAG, "Invalid token");
        } else {
            Snackbar.make(findViewById(android.R.id.content), sdkResult.getError().getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    protected void startSelfiePreview(String imageContent) {
        Intent i = new Intent(this, LivenessCheckPreviewActivity.class);
        i.putExtra("selfie", imageContent);
        mStartForResult.launch(i);
    }
}