package com.qhh.camerademo.activity;

import android.hardware.Camera;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.qhh.camerademo.R;
import com.sensetime.cameralibrary.CameraConfig;
import com.sensetime.cameralibrary.camera1.CameraHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.sensetime.cameralibrary.utils.FileUtils.MEDIA_TYPE_IMAGE;
import static com.sensetime.cameralibrary.utils.FileUtils.getOutputMediaFile;

public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_capture);
        initView();
    }

    private void initView() {
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
    }

    private void openCamera(){
        CameraConfig config = new CameraConfig.Builder()
                .setCameraType(CameraConfig.BACK_CAMERA)
                .setPreviewHeight(mSurfaceView.getMeasuredHeight())
                .setPreviewWidth(mSurfaceView.getMeasuredWidth())
                .setSurfaceHolder(mSurfaceView.getHolder())
                .builer();

        mCamera = CameraHelper.getInstance().openCamera(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void click(View view) {
        switch(view.getId()){
            case R.id.btn_capture:
                mCamera.takePicture(null, null, mPictureCallback);
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraHelper.getInstance().release();
    }
}
