package com.example.akal.sampleproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.akal.sampleproject.R;
import com.example.akal.sampleproject.model.Util;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = getSharedPreferences(Util.PREFS_NAME,MODE_PRIVATE);
        boolean check = preferences.getBoolean(Util.KEY_LOGREG,false);

        if(check){
            handler.sendEmptyMessageDelayed(102,3000);
        }else {
            handler.sendEmptyMessageDelayed(101,3000);
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 101){
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}
