package com.qhh.camerademo.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.qhh.camerademo.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Camera2Activity extends AppCompatActivity {

    private static final String TAG = Camera2Activity.class.getSimpleName();

    private static final int CAMERA_TYPE = CameraCharacteristics.LENS_FACING_FRONT;

    private CameraDevice mCameraDevice;
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private CameraHandler mCameraHandler;
    private CameraCaptureSession mPreviewSession;
    private CaptureRequest mPreviewRequest;

    private ImageReader mImageReader;

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = reader -> {

        Image image = reader.acquireNextImage();
        if(image.getFormat() == ImageFormat.YUV_420_888){

            Image.Plane[] planes = image.getPlanes();

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        mTextureView = findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurfaceTexture = surface;
                initCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }

    private void initCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String[] cameraIdList = null;

        try {

            cameraIdList = cameraManager.getCameraIdList();

            for (String cameraId : cameraIdList) {
                Log.d(TAG, "cameraId is " + cameraId);

                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                Log.d(TAG, "cameraId cameraCharacteristics is " + cameraCharacteristics.toString());

                Integer cameraType = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(cameraType != null && cameraType != CAMERA_TYPE){
                    continue;
                }

                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                Size[] outputSizes = map.getOutputSizes(ImageFormat.YUV_420_888);

                Size largeSize = Collections.max(Arrays.asList(outputSizes), new CompareSizesByArea());
                Log.d(TAG,"size width = " + largeSize.getWidth() + ", height = " + largeSize.getHeight());

                mImageReader = ImageReader.newInstance(largeSize.getWidth(), largeSize.getHeight(), ImageFormat.YUV_420_888, 2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,mCameraHandler);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (cameraIdList == null) {
            return;
        }

        CameraStateCallback cameraStateCallback = new CameraStateCallback();
        mCameraHandler = new CameraHandler();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            cameraManager.openCamera(cameraIdList[0], cameraStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void createPreviewSession() {

        CaptureRequest.Builder previewRequest = null;

        try {
            previewRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface surface = new Surface(mSurfaceTexture);

            previewRequest.addTarget(new Surface(mSurfaceTexture));

            CaptureRequest.Builder finalPreviewRequest = previewRequest;

            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.d(TAG, "camera onConfigured ");

                    // 相机已经关闭
                    if (null == mCameraDevice) {
                        return;
                    }
                    // 会话准备好后，我们开始显示预览
                    mPreviewSession = session;
                    try {
                        // 自动对焦应
                        finalPreviewRequest.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 闪光灯
                        //setAutoFlash(mPreviewRequestBuilder);
                        // 开启相机预览并添加事件
                        mPreviewRequest = finalPreviewRequest.build();
                        //发送请求
                        mPreviewSession.setRepeatingRequest(mPreviewRequest,
                                null, mCameraHandler);
                        Log.i(TAG," 开启相机预览并添加事件");
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "camera onConfigureFailed ");
                }
            },mCameraHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    class CameraStateCallback extends CameraDevice.StateCallback {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.i(TAG,"CameraStateCallback onOpened");
            mCameraDevice = camera;
            createPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.i(TAG,"CameraStateCallback onDisconnected");
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG,"CameraStateCallback onError " + error);
            camera.close();
            mCameraDevice = null;
        }
    }

    class CameraHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
