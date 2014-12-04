package com.seg2.kcl2d;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


public class HomeScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    public void showMain(View view) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putBoolean("shouldHideHomeScreen", true);
        preferenceEditor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
