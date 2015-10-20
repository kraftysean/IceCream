package com.tasty.icecream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Show splash screen before main activity with timer set to 3 seconds.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity of the app
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity - don't want to go back to the splash screen :)
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}