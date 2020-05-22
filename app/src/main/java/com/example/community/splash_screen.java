package com.example.community;

import android.content.Intent;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        LogoLauncher logoLauncher=new LogoLauncher();
        logoLauncher.start();


    }
    private class LogoLauncher extends Thread{
    public void run(){

        try {
        sleep(1000);
             }
        catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
        startActivity(new Intent(splash_screen.this, MainActivity.class));
        splash_screen.this.finish();
}
    }
}
