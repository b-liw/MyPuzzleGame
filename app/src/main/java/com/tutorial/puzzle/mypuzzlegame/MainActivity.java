package com.tutorial.puzzle.mypuzzlegame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView cameraImageView;
    private Button startButton;
    private static final int CAMERA_PHOTO_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraImageView = findViewById(R.id.camera_photo);
        startButton = findViewById(R.id.start_button);
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, CAMERA_PHOTO_REQUEST_CODE);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraImageView.getDrawable() == null) {
                    Toast.makeText(MainActivity.this, "Aby przejść dalej, wymagane jest zdjęcie. Naciśnij szare pole.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                    Bitmap bitmap = ((BitmapDrawable) cameraImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    intent.putExtra("photo_camera_id", outStream.toByteArray());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraImageView.post(new Runnable() {
            @Override
            public void run() {
                if (CAMERA_PHOTO_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, cameraImageView.getWidth(), cameraImageView.getHeight(), false);
                    cameraImageView.setImageBitmap(scaledBitmap);
                }
            }
        });

    }
}
