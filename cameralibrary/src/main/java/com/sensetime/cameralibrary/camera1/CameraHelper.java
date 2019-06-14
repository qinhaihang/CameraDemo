package com.sensetime.cameralibrary.camera1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
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

        Camera.Size fitPreviewSize = getFitPreviewSize(parameters);
        if (DEBUG) {
            Log.i(TAG, "fitPreviewSize, width: " + fitPreviewSize.width + ", height: " + fitPreviewSize.height);
        }
        mPreviewWidth = fitPreviewSize.width;
        mPreviewHeight = fitPreviewSize.height;
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);

        if(config.getPreviwFormat() != 0){
            parameters.setPreviewFormat(config.getPreviwFormat());
        }

        mCamera.setDisplayOrientation(config.getOrientation());

        mCamera.setParameters(parameters);

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

        mCamera.startPreview();

        return mCamera;
    }

    public void release(){
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private float getPreviewScale(int viewWidth,int viewHeight){
        float scale;
        if (viewWidth > viewHeight) {
            scale = viewHeight / viewWidth;
        } else {
            scale = viewWidth / viewHeight;
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

}
