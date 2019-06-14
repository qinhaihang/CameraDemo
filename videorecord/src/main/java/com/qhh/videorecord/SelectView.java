package com.qhh.videorecord;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author admin
 * @version $Rev$
 * @time 2018/8/31 15:05
 * @des 用于自定义小视频拍摄页面中录制结束后，选择和取消控件
 * @packgename com.hbjs.renrenshengyi.test
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */

public class SelectView extends RelativeLayout {

    private Context mContext;
    public ImageView mSelect; //选择
    public ImageView mCancel; //取消

    public SelectView(Context context) {
        this(context, null, 0);
    }

    public SelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        View.inflate(context, R.layout.select_view, this);
        mSelect = findViewById(R.id.select);
        mCancel = findViewById(R.id.cancel);
    }

    public void startAnim() {
        setVisibility(VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mCancel, "translationX", 0, -300),
                ObjectAnimator.ofFloat(mSelect, "translationX", 0, 300)
        );
        set.setDuration(250).start();
    }

    public void stopAnim() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mCancel, "translationX", -300, 0),
                ObjectAnimator.ofFloat(mSelect, "translationX", 300, 0)
        );
        set.setDuration(250).start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
