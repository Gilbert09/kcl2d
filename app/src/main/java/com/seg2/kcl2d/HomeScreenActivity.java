package com.seg2.kcl2d;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class HomeScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    public void showMain(View view) {
        Intent i = new Intent(this, MainActivity.class);

        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putBoolean("shouldHideHomeScreen", true);
        preferenceEditor.apply();

        startActivity(i);
    }
}
