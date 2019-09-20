package com.sensetime.cameralibrary.camera1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;

import com.sensetime.cameralibrary.BuildConfig;
import com.sensetime.cameralibrary.CameraConfig;

import java.io.IOException;
import java.util.List;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/6/13 16:44
 * @des
 * @packgename com.sensetime.cameralibrary.camera1
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();
    private boolean DEBUG = BuildConfig.LOG_DEBUG;

    private static CameraHelper instance;

    private int mResolution = 480; // 分辨率大小，以预览高度为标准(320, 480, 720, 1080...)
    private float mPreviewScale; // 预览显示的比例(4:3/16:9)
    public int mPreviewWidth; // 预览宽度
    public int mPreviewHeight; // 预览高度
    private Camera mCamera;

    private CameraCallback mCameraCallback;
    private byte[] mBuffer;

    public static CameraHelper getInstance() {
        if (instance == null) {
            synchronized (CameraHelper.class) {
                if (instance == null) {
                    instance = new CameraHelper();
                }
            }
        }
        return instance;
    }

    /** Check if this device has a camera */
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
       }
    }

    /**
     * 针对 TextureView
     * @param config
     * @return
     */
    public Camera openCamera(CameraConfig config){

        if(config == null){
            return null;
        }

        mCamera = Camera.open(config.getCameraType());
        Camera.Parameters parameters = mCamera.getParameters();

        mPreviewScale = getPreviewScale(config.getPreviewWidth(),config.getPreviewHeight());

        setPreviewSize(parameters);

        setFormat(config, parameters);

        setFocus(config,parameters);

        mCamera.setDisplayOrientation(config.getOrientation());

        mCamera.setParameters(parameters);

        setSurface(config);

        if(config.isPreviewBuffer()){
            initPreviewBuffer();
        }else{
            initPreviewCallback();
        }

        mCamera.startPreview();

        return mCamera;
    }

    private void setFocus(CameraConfig config,Camera.Parameters parameters) {

        if(TextUtils.isEmpty(config.getFocusMode())){
            return;
        }

        List<String> supportedFocusModes = parameters.getSupportedFocusModes();

        if(supportedFocusModes.contains(config.getFocusMode())){
            parameters.setFocusMode(config.getFocusMode());
        }else{
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        }

    }

    private void setSurface(CameraConfig config) {
        try {

            if(config.getSurfaceTexture() != null){
                mCamera.setPreviewTexture(config.getSurfaceTexture());
            }

            if(config.getSurfaceHolder() != null){
                mCamera.setPreviewDisplay(config.getSurfaceHolder());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFormat(CameraConfig config, Camera.Parameters parameters) {
        if(config.getPreviwFormat() != 0){
            parameters.setPreviewFormat(config.getPreviwFormat());
        }

        parameters.setPictureFormat(ImageFormat.JPEG);
        if(config.getPictureWidth() == 0 || config.getPictureHeight() == 0){
            parameters.setPictureSize(mPreviewWidth,mPreviewHeight);
        }else{
            parameters.setPictureSize(config.getPictureWidth(),config.getPictureHeight());
        }
    }

    private void setPreviewSize(Camera.Parameters parameters) {
        Camera.Size fitPreviewSize = getFitPreviewSize(parameters);
        if (DEBUG) {
            Log.i(TAG, "fitPreviewSize, width: " + fitPreviewSize.width + ", height: " + fitPreviewSize.height);
        }
        /*mPreviewWidth = fitPreviewSize.width;
        mPreviewHeight = fitPreviewSize.height;*/
        /*mPreviewWidth = 1920;
        mPreviewHeight = 1080;*/
        mPreviewWidth = 1280;
        mPreviewHeight = 720;
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
    }

    public float getPreviewScale(){
        return mPreviewScale;
    }

    public void setCameraCallback(CameraCallback cameraCallback) {
        mCameraCallback = cameraCallback;
    }

    public void release(){
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private float getPreviewScale(int viewWidth,int viewHeight){
        float scale;
        if (viewWidth > viewHeight) {
            scale = (float) viewHeight / (float) viewWidth;
        } else {
            scale = (float) viewWidth / (float) viewHeight;
        }

        return scale;
    }

    /**
     * 具体计算最佳分辨率大小的方法
     */
    private Camera.Size getFitPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes(); // 获取支持的预览尺寸大小
        int minDelta = Integer.MAX_VALUE; // 最小的差值，初始值应该设置大点保证之后的计算中会被重置
        int index = 0; // 最小的差值对应的索引坐标
        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size previewSize = previewSizes.get(i);
            if (DEBUG) {
                Log.d(TAG, "SupportedPreviewSize, width: " + previewSize.width + ", height: " + previewSize.height);
            }
            // 找到一个与设置的分辨率差值最小的相机支持的分辨率大小
            if (previewSize.width * mPreviewScale == previewSize.height) {
                int delta = Math.abs(mResolution - previewSize.height);
                if (delta == 0) {
                    return previewSize;
                }
                if (minDelta > delta) {
                    minDelta = delta;
                    index = i;
                }
            }
        }
        return previewSizes.get(index); // 默认返回与设置的分辨率最接近的预览尺寸
    }

    private void initPreviewBuffer() {
        if (mCamera != null) {
            mBuffer = new byte[mPreviewWidth * mPreviewHeight * 3 / 2]; // 初始化预览缓冲数据的大小,NV21格式大小
            mCamera.addCallbackBuffer(mBuffer); // 将此预览缓冲数据添加到相机预览缓冲数据队列里
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                    if(mCameraCallback != null){
                        mCameraCallback.onPreviewFrame(data);
                    }
                }
            }); // 设置预览的回调
        }
    }

    private void initPreviewCallback() {
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if(mCameraCallback != null){
                    mCameraCallback.onPreviewFrame(data);
                }
            }
        });
    }

    public interface CameraCallback{
        void onPreviewFrame(byte[] data);
    }

}
