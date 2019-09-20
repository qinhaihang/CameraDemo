package com.qhh.camerademo.activity;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import com.qhh.camerademo.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

public class CameraXActivity extends AppCompatActivity {

    private TextureView mViewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);
        mViewFinder = findViewById(R.id.texture_view);
        mViewFinder.postDelayed(this::startCamera, 1000);
        mViewFinder.addOnLayoutChangeListener(
                (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> updateTransform());
    }

    private void startCamera() {

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                //.setTargetAspectRatio(new Rational(1, 1))  //纵横比
                .setTargetResolution(new Size(1920, 1080)) // 分辨率
                .build();

        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {

            ViewGroup parent = (ViewGroup) mViewFinder.getParent();
            parent.removeView(mViewFinder);
            parent.addView(mViewFinder, 0);
            SurfaceTexture surfaceTexture = output.getSurfaceTexture();
            mViewFinder.setSurfaceTexture(surfaceTexture);

            updateTransform();
        });

        CameraX.bindToLifecycle(this, preview);
    }

    private void updateTransform() {

        Matrix matrix = new Matrix();

        float centerX = mViewFinder.getWidth() / 2f;
        float centerY = mViewFinder.getHeight() / 2f;

        int rotation = mViewFinder.getDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
            default:
                break;
        }

        matrix.postRotate(-rotation,centerX,centerY);
        mViewFinder.setTransform(matrix);
    }

}
