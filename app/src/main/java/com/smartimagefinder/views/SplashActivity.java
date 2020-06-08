package com.smartimagefinder.views;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.smartimagefinder.R;
import com.smartimagefinder.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isTaskRoot())
            openSearchActivity();

        finish();
    }
}
