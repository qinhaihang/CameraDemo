package com.qhh.camerademo.activity;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qhh.camerademo.R;
import com.sensetime.cameralibrary.CameraConfig;
import com.sensetime.cameralibrary.camera1.CameraHelper;

public class OpenGLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl);

        CameraHelper.getInstance().setCameraCallback(data -> {
            Log.d("qhh",">>> data size = " + data.length);

        });

        openCamera(new SurfaceTexture(0),1920,1080);
    }

    private void openCamera(SurfaceTexture surfaceTexture, int w, int h) {

        CameraConfig config = new CameraConfig.Builder()
                .setCameraType(CameraConfig.BACK_CAMERA)
                .setPreviewWidth(w)
                .setPreviewHeight(h)
                .setSurfaceTexture(surfaceTexture)
                .setPreviewBuffer(false)
                .builer();

        CameraHelper.getInstance().openCamera(config);
    }
}
