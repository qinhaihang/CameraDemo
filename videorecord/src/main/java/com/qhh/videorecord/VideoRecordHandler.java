package com.qhh.videorecord;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author admin
 * @version $Rev$
 * @time 2018/8/31 11:47
 * @des ${TODO}
 * @packgename com.hbjs.renrenshengyi.test
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class VideoRecordHandler extends Handler {

    private WeakReference<VideoRecordView> mWeakReference = null;

    public VideoRecordHandler(VideoRecordView videoRecordView) {
        mWeakReference = new WeakReference<VideoRecordView>(videoRecordView);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mWeakReference == null || mWeakReference.get() == null)
            return;
        final VideoRecordView videoRecordView = mWeakReference.get();
        switch (msg.what) {
            case VideoRecordView.MSG_START_LONG_RECORD:
                if (videoRecordView.onRecordListener != null) {
                    videoRecordView.onRecordListener.OnRecordStartClick();
                }
                //内外圆动画，内圆缩小，外圆放大
                videoRecordView.startAnimation(videoRecordView.mExCircleRadius,
                        videoRecordView.mExCircleRadius * videoRecordView.excicleMagnification,
                        videoRecordView.mInnerCircleRadius,
                        videoRecordView.mInnerCircleRadius * videoRecordView.excicleMagnification);
                break;
            default:
                break;
        }
    }
}
