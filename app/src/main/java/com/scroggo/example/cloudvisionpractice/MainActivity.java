package com.scroggo.example.cloudvisionpractice;

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

public class MainActivity extends AppCompatActivity {
    private static final int PICTURE_REQUEST = 1;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageView);
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
            }
        } else {
            Toast.makeText(this, "Picture capture failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
