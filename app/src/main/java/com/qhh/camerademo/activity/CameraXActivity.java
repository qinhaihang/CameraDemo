package com.qhh.camerademo.activity;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import com.qhh.camerademo.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageInfo;
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

        //预览
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
            Log.d("qhh_camera",">>> PreviewOutput ");
        });

        //数据流处理
        ImageAnalysisConfig analysisConfig = new ImageAnalysisConfig.Builder()
                .setTargetResolution(new Size(1280, 720))
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(analysisConfig);

        imageAnalysis.setAnalyzer((image, rotationDegrees) -> {
            int format = image.getFormat();
            Image.Plane[] planes = image.getImage().getPlanes();
            int length = planes.length;
            ImageInfo imageInfo = image.getImageInfo();
            for (int i = 0; i < length; i++) {

            }
            Log.i("qhh_camera",">>>> format = " + format + ", planes len = " + length);
        });

        CameraX.bindToLifecycle(this, imageAnalysis,preview);
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
