package com.smartimagefinder.base;

import android.content.Intent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.smartimagefinder.views.SearchActivity;
import com.smartimagefinder.views.SearchDetailActivity;

public class BaseActivity extends AppCompatActivity {

    public void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSearchDetailActivity(String clickedImageUrl, ImageView imgView, String sharedElementName) {
        Intent intent = new Intent(this, SearchDetailActivity.class);
        intent.putExtra(SearchDetailActivity.KEY_IMAGE_URL, clickedImageUrl);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imgView, sharedElementName);
        startActivity(intent, options.toBundle());
    }
}