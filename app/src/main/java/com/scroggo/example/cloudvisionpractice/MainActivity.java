package com.scroggo.example.cloudvisionpractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PICTURE_REQUEST = 1;

    private ImageView mImageView;
    private FirebaseVisionFaceDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageView);

        FirebaseVisionFaceDetectorOptions options
                = new FirebaseVisionFaceDetectorOptions.Builder()
                .setClassificationMode(
                        FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        mDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    public void onClick(View view) {
        if (view == mImageView) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, PICTURE_REQUEST);
        }
    }

    private static void log(String msg) {
        Log.d("FACE", msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICTURE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bm = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(bm);

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bm);
                mDetector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                                log("found " + firebaseVisionFaces.size() + " faces");
                                for (FirebaseVisionFace face : firebaseVisionFaces) {
                                    log("next face:");
                                    float smileProbability = face.getSmilingProbability();
                                    if (smileProbability
                                            == FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                        log("\tDid not compute smiling probability!");
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "smiling probability: " + smileProbability,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,
                                        "Face detection failed!",
                                        Toast.LENGTH_SHORT).show();
                                log("face detection failed with " + e);
                                e.printStackTrace();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Picture capture failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
