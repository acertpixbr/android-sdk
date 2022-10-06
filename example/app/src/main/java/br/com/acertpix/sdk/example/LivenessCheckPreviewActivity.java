package br.com.acertpix.sdk.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class LivenessCheckPreviewActivity extends AppCompatActivity {

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_check_preview);
        getSupportActionBar().hide();

        String selfie = (String) getIntent().getSerializableExtra("selfie");

        setPicture(selfie);

        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            finish();
        });
    }

    private void setPicture(String path) {
        ImageView preview = findViewById(R.id.selfie_preview);

        preview.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (preview.getWidth() > 0 && preview.getHeight() > 0) {
                int targetW = preview.getWidth();
                int targetH = preview.getHeight();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                byte[] decodedString = Base64.decode(path, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                // bmOptions.inPurgeable = true;
                // inBitmap only works with mutable bitmaps, so force the decoder to
                // return mutable bitmaps.
                bmOptions.inMutable = true;
                bmOptions.inBitmap = bmp;


                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, bmOptions);
                preview.setImageBitmap(bitmap);
            }
        });
    }

    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        super.onDestroy();
    }
}