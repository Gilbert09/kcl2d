package com.seg2.kcl2d;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class LaunchHelperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPreferences", MODE_PRIVATE);

        boolean shouldHideHomeScreen = sharedPreferences.getBoolean("shouldHideHomeScreen", false);

        if (shouldHideHomeScreen) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new Intent(this, MainActivity.class));
        } else {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(new Intent(this, HomeScreenActivity.class));
        }
    }
}
