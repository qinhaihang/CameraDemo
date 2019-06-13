package com.qhh.camerademo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.qhh.camerademo.Activity.NormalCamera1Activity;
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
                        Manifest.permission_group.STORAGE
                );

    }

    public void click(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_normal:
                startActivity(new Intent(this, NormalCamera1Activity.class));
                break;
            default:
                break;
        }
    }
}
