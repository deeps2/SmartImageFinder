package com.smartimagefinder.views;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smartimagefinder.R;
import com.smartimagefinder.base.BaseActivity;
import com.smartimagefinder.utils.AppUtils;

public class SearchDetailActivity extends BaseActivity {

    public static final String KEY_IMAGE_URL = "KEY_IMAGE_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        AppUtils.changeStatusBarColor(this, R.color.status_bar_color);
        ImageView imgView = findViewById(R.id.image_full_screen);
        Glide.with(this).load(getIntent().getStringExtra(KEY_IMAGE_URL)).fitCenter().into(imgView);
    }
}