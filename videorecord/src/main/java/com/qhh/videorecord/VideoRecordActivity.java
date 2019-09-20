package com.qhh.videorecord;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;

/**
 * @des 拍摄小视频、图片自定义页面
 */

public class VideoRecordActivity extends AppCompatActivity {

    //@BindView(R.id.surface_view)
    SurfaceView surface_view;
    //@BindView(R.id.video_record)
    VideoRecordView video_record;
    //@BindView(R.id.select_view)
    SelectView select_view;

    private VideoRecordHelper mVideoRecordHelper;
    private File mRecordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:设置全屏之后，确认取消自定义按钮动画超出显示
        //requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_video_record);
        //ButterKnife.bind(this);
        //PermissionUtils.checkMediaRecorderPermission(VideoRecordActivity.this, this);
        surface_view = findViewById(R.id.surface_view);
        video_record = findViewById(R.id.video_record);
        select_view = findViewById(R.id.select_view);

        mVideoRecordHelper = new VideoRecordHelper(true);
        mVideoRecordHelper.setSurfaceView(surface_view);

        video_record.setOnRecordListener(new VideoRecordView.OnRecordListener() {

            @Override
            public void onShortClick() {
                super.onShortClick();
                //短暂点击
                //ToastUtils.show(VideoRecordActivity.this, "点击事件");
            }

            @Override
            public void OnRecordStartClick() {
                //开始录制
                mVideoRecordHelper.record();
            }

            @Override
            public void OnFinish(int resultCode) {
                switch (resultCode) {
                    case VideoRecordView.SHORT_RECORD:
                        //ToastUtils.show(VideoRecordActivity.this, "录制时间太短");
                        break;
                    case VideoRecordView.NORMAL_RECORD:
                        mVideoRecordHelper.stop();
                        //ToastUtils.show(VideoRecordActivity.this, "视频地址1：" + mVideoRecordHelper.getmRecordFile());
                        select_view.startAnim();
                        //TODO:暂时是选择屏蔽录像控件按钮
                        video_record.setClickable(false);
                        break;
                    case VideoRecordView.TIMEOUT_RECORD:
                        mVideoRecordHelper.stop();
                        //ToastUtils.show(VideoRecordActivity.this, "视频地址2：" + mVideoRecordHelper.getmRecordFile());
                        select_view.startAnim();
                        video_record.setClickable(false);
                        break;
                    default:
                        break;
                }
            }
        });

        select_view.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择录制好的视频
                select_view.stopAnim();
                mRecordFile = mVideoRecordHelper.getmRecordFile();
                video_record.setClickable(true);
            }
        });

        select_view.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消录制好的视频
                select_view.stopAnim();
                mVideoRecordHelper.getmRecordFile().delete();
                video_record.setClickable(true);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoRecordHelper.stop();
    }

    //    @Override
    //    public void requestPermissionSuccess(Boolean aBoolean) {
    //        Logger.d("VideoRecordHelper", "requestPermissionSuccess == " + aBoolean);
    //
    //    }
    //
    //    @Override
    //    public void requestPermissionFailure(Boolean aBoolean) {
    //    }

}
