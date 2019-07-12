package com.qhh.camerademo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qhh.camerademo.activity.CaptureActivity;
import com.qhh.camerademo.activity.NormalCamera1Activity;
import com.qhh.camerademo.activity.OpenGLActivity;
import com.qhh.permission.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionHelper.getInstance()
                .init(this)
                .checkPermission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                );

    }

    public void click(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_normal:
                startActivity(new Intent(this, NormalCamera1Activity.class));
                break;
            case R.id.btn_capture:
                startActivity(new Intent(this, CaptureActivity.class));
                break;
            case R.id.btn_gl:
                startActivity(new Intent(this, OpenGLActivity.class));
                break;
            default:
                break;
        }
    }
}
