package com.qhh.camerademo.activity;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;

import com.qhh.camerademo.R;
import com.sensetime.cameralibrary.CameraConfig;
import com.sensetime.cameralibrary.camera1.CameraHelper;

public class NormalCamera1Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener, CameraHelper.CameraCallback {

    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_normal_camera1);
        initView();

        CameraHelper.getInstance().setCameraCallback(this);
    }

    private void initView() {
        mTextureView = findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(this);
    }

    private void openCamera(SurfaceTexture surfaceTexture,int w,int h) {

        boolean cameraHardware = CameraHelper.getInstance().checkCameraHardware(this);

        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i,cameraInfo);
            int facing = cameraInfo.facing;
            Log.d("qhh_camera",">>> cameraId " + i +" is " + facing);
        }

        CameraConfig config = new CameraConfig.Builder()
                //.setCameraType(CameraConfig.FRONT_CAMERA)
                .setCameraType(CameraConfig.BACK_CAMERA)
                .setPreviewWidth(w)
                .setPreviewHeight(h)
                .setSurfaceTexture(surfaceTexture)
                .builer();

        CameraHelper.getInstance().openCamera(config);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CameraHelper.getInstance().release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraHelper.getInstance().release();
    }

    @Override
    public void onPreviewFrame(byte[] data) {
        Log.i("qhh_camera",">>> data size " + data.length);
    }
}
