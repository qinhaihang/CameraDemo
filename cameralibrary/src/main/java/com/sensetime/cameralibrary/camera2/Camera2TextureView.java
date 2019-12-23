package com.sensetime.cameralibrary.camera2;

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
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;

/**
 * @author qinhaihang
 * @time 2019/12/23 22:39
 * @des
 * @packgename com.sensetime.cameralibrary.camera2
 */
public class Camera2TextureView extends TextureView implements TextureView.SurfaceTextureListener, ImageReader.OnImageAvailableListener {

    private static final String TAG = Camera2TextureView.class.getSimpleName();

    private Context mContext;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;

    /**
     * 配置开启哪一个摄像头
     */
    private int mCameraType = CameraCharacteristics.LENS_FACING_FRONT;

    /**
     * 获取预览帧数据
     */
    private ImageReader mImageReader;

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * 开启的cameraId
     */
    private String mCameraId;

    private SurfaceTexture mSurfaceTexture;
    private CameraCaptureSession mPreviewSession;
    private CaptureRequest mPreviewRequest;

    public Camera2TextureView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public Camera2TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Camera2TextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        setSurfaceTextureListener(this);
        initCamera();
    }

    private void initCamera() {
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);

        String[] cameraIdList = null;

        try {
            cameraIdList = mCameraManager.getCameraIdList();

            if (cameraIdList == null) {
                Log.e(TAG, "device has not camera");
                return;
            }

            for (String cameraId : cameraIdList) {

                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);

                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);

                if(facing == null || facing != mCameraType){
                    continue;
                }

                mCameraId = cameraId;

                StreamConfigurationMap configurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                Size[] outputSizes = configurationMap.getOutputSizes(ImageFormat.YUV_420_888);

                Size largeSize = Collections.max(Arrays.asList(outputSizes), new CompareSizesByArea());

                mImageReader = ImageReader.newInstance(largeSize.getWidth(), largeSize.getHeight(), ImageFormat.YUV_420_888, 2);
                mImageReader.setOnImageAvailableListener(this,mBackgroundHandler);

            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(String cameraId){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            CameraStateCallback cameraStateCallback = new CameraStateCallback();
            mCameraManager.openCamera(cameraId, cameraStateCallback, mBackgroundHandler);
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

            final CaptureRequest.Builder finalPreviewRequest = previewRequest;

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
                                null, mBackgroundHandler);
                        Log.i(TAG," 开启相机预览并添加事件");
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "camera onConfigureFailed ");
                }
            },mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTexture = surface;
        openCamera(mCameraId);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * ImageReader setOnImageAvailableListener
     * @param reader
     */
    @Override
    public void onImageAvailable(ImageReader reader) {

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
}
