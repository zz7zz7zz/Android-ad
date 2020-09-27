package com.module.addemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.module.ad.main.AdMain;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdMain.getInstance().preLoad(1,"google.admob");
        AdMain.getInstance().show(1,"google.admob");

        AdMain.getInstance().preLoad(2,"huawei");
        AdMain.getInstance().show(2,"huawei");
    }
}
