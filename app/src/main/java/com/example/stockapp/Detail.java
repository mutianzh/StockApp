package com.example.stockapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolBar);
        myToolbar.setTitleTextAppearance(this, R.style.BoldTextAppearance);
        myToolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get search quote
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        NestedScrollView nestedScrollView = findViewById(R.id.alldetails);
        LinearLayout progressBar = findViewById(R.id.progressbar);

        nestedScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        setProgressBarIndeterminateVisibility(true);

//        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
//        linlaHeaderProgress.setVisibility(View.VISIBLE);

    }
}