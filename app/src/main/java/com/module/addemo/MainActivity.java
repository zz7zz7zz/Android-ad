package com.module.addemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.module.ad.main.AdMain;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        AdMain.getInstance().preLoad(this,1,"google.admob");
//        AdMain.getInstance().show(this,1,"google.admob");

        AdMain.getInstance().preLoad(this,2,"huawei");
        AdMain.getInstance().show(this,2,"huawei",(ViewGroup) findViewById(R.id.ad_parent_banner));
    }
}
