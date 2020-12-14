package com.module.addemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.module.ad.main.AdMain;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.loadAd).setOnClickListener(this);
        findViewById(R.id.showAd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loadAd:
                AdMain.getInstance().preLoad(this,1,"onClick");
                break;

            case R.id.showAd:
                AdMain.getInstance().show(this,1,null,null);
//                AdMain.getInstance().show(this, 1, null, 10000, new OnAdShowResultListener() {
//                    @Override
//                    public void onSuccess() {
//                        Log.v("MainActivity","show onSuccess");
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        Log.v("MainActivity","show onFailed");
//                    }
//                });
                break;
        }
    }
}
