package com.qhh.camerademo.activity;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.qhh.camerademo.R;
import com.qhh.camerademo.opengl.GLFrameRenderer;
import com.sensetime.cameralibrary.CameraConfig;
import com.sensetime.cameralibrary.camera1.CameraHelper;

public class OpenGLActivity extends AppCompatActivity {


    private GLFrameRenderer mGLRenderer;

    private boolean isFirstFrame = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl);

        initGLSurfaceView();

        CameraHelper.getInstance().setCameraCallback(data -> {
            Log.d("qhh",">>> data size = " + data.length);


            /*if (isFirstFrame) {
                isFirstFrame = false;
                mGLRenderer.update(1080, 1920);
            } else {
                mGLRenderer.update(data);
            }*/

        });

        openCamera(new SurfaceTexture(0),1920,1080);
    }

    private void initGLSurfaceView() {

        GLSurfaceView gl = findViewById(R.id.gl);
        gl.setEGLContextClientVersion(2);
        mGLRenderer = new GLFrameRenderer(this, gl, getDisplayMetrics(this));
        gl.setRenderer(mGLRenderer);
        gl.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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

    public DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics;
    }
}
