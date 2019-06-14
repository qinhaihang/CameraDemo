package com.qhh.videorecord;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;

/**
 * @author admin
 * @version $Rev$
 * @time 2018/8/28 18:12
 * @des ${TODO}
 * @packgename com.hbjs.renrenshengyi.test
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class VideoRecordHelper implements SurfaceHolder.Callback, MediaRecorder.OnErrorListener {

    private static final String TAG = "VideoRecordHelper";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private boolean isOpenCamera;  //是否一开始就打开摄像头
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private int mWidth = 320;
    private int mHeight = 240;
    private File mRecordFile;
    private OnRecordFinishListener mOnRecordFinishListener;

    public VideoRecordHelper() {
    }

    public VideoRecordHelper(boolean isOpenCamera) {
        this.isOpenCamera = isOpenCamera;
    }

    public VideoRecordHelper(SurfaceView surfaceView, boolean isOpenCamera) {
        mSurfaceView = surfaceView;
        this.isOpenCamera = isOpenCamera;
        setSurfaceView(surfaceView);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.d(TAG, "surfaceCreated start");
        //初始化摄像头
        if (!isOpenCamera)
            return;
        initCamera();
        Logger.d(TAG, "surfaceCreated end");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d(TAG, "surfaceDestroyed start");
        //释放摄像头
        if (!isOpenCamera)
            return;
        releaseCamera();
        Logger.d(TAG, "surfaceDestroyed end");
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        //录制监听错误回调,MediaRecorder
        try {
            if (mr != null)
                mr.reset();
        } catch (Exception e) {
            e.printStackTrace();
            //如果录制有错误，需要做相关操作来提醒用户
        }
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        //获取Holder
        mSurfaceHolder = mSurfaceView.getHolder();
        //设置分辨率
        //mSurfaceHolder.setFixedSize(previewWidth, previewHeight);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    /**
     * 初始化摄像头
     */
    @SuppressLint("NewApi")
    private void initCamera() {

        //先释放相机资源
        if (null != mCamera) {
            releaseCamera();
        }

        try {

            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mSurfaceHolder);

        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
        }

        if (null == mCamera)
            return;

        mCamera.startPreview();
        mCamera.unlock();
    }

    /**
     * 释放摄像头资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 初始化摄像头基本参数
     */
    private void initRecord() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源
        //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//音频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//音频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//视频输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//音频格式
        mMediaRecorder.setVideoSize(mWidth, mHeight);//设置分辨率
        //设置帧频率，可以按照需求适当调整，这个直接相关录制视频的大小
        mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024);
        mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// 视频录制格式
        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            //提示录制权限存在问题，打开相关权限
        }
    }

    /**
     * 创建录制文件存放的文件夹以及录制文件名称
     */
    private void createRecordDir() {
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "mmxs/video/");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        File vecordDir = sampleDir;
        // 创建文件
        try {
            mRecordFile = File.createTempFile("mmxs_" + System.currentTimeMillis(), ".mp4", vecordDir); //mp4格式
        } catch (IOException e) {
            //打开文件操作相关权限
        }
    }

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     */
    public void record(OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        if (!isOpenCamera)// 如果未打开摄像头，则打开
            initCamera();
        initRecord();
        //mTimeCount = 0;// 时间计数器重新赋值
        //mTimer = new Timer();
        //mTimer.schedule(new TimerTask() {
        //    @Override
        //    public void run() {
        //        // TODO Auto-generated method stub
        //        mTimeCount++;
        //        mProgressBar.setProgress(mTimeCount);// 设置进度条
        //        if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
        //            stop();
        //            if (mOnRecordFinishListener != null)
        //                mOnRecordFinishListener.onRecordFinish();
        //        }
        //    }
        //}, 0, 1000);
    }

    /**
     * 开始录制视频
     * 没有回调监听
     */
    public void record() {
        createRecordDir();
        if (!isOpenCamera)// 如果未打开摄像头，则打开
            initCamera();
        initRecord();
    }

    /**
     * 停止录制
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        releaseCamera();
        isOpenCamera = false;
    }

    public void stopRecord() {
        //mProgressBar.setProgress(0);
        //if (mTimer != null)
        //    mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                return;
            }
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    //释放资源
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (Exception e) {
                return;
            }
        }
        mMediaRecorder = null;
    }

    /**
     * 获取存储路径
     *
     * @return
     */
    public File getmRecordFile() {
        return mRecordFile;
    }

}
