package com.hose.aureliano.project.done.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Splash activity.
 * <p>
 * Date: 6/13/2018.
 *
 * @author Uladzislau_Shalamitski
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, TasksActivity.class));
        finish();
    }
}
