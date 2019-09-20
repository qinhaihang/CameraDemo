package com.qhh.camerademo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qhh.camerademo.Constants;
import com.qhh.camerademo.activity.NormalCamera1Activity;


public class SDStatusReceiver extends BroadcastReceiver {

    private static final String TAG = SDStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(Intent.ACTION_MEDIA_MOUNTED)
                || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                || action.equals(Intent.ACTION_MEDIA_REMOVED)){
            Log.d(TAG,"接收到开机广播 = " + action);
            Intent intent1 = new Intent(context, NormalCamera1Activity.class);
            intent1.putExtra(Constants.INTENT_TYPE,Constants.SD_STATUS);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent1);
        }

    }
}
