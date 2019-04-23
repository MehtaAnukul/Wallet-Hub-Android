package com.anukul.wallethub.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.anukul.wallethub.R;

public class AboutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("About");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
