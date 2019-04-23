package com.anukul.wallethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.anukul.wallethub.R;

public class MainActivity extends AppCompatActivity {
    private TextView walletHubTv;
    private TextView taglineTv;
    private ImageView logoImage;
    Animation walletHubAnim,taglineAnim,logoImgAnim;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        walletHubTv = findViewById(R.id.activity_main_walletHubTv);
        taglineTv = findViewById(R.id.activity_main_taglineTv);
        logoImage = findViewById(R.id.activity_main_logoImg);

        //load Animation
        walletHubAnim = AnimationUtils.loadAnimation(this,R.anim.charanim);
        taglineAnim = AnimationUtils.loadAnimation(this,R.anim.charanim);
        logoImgAnim = AnimationUtils.loadAnimation(this,R.anim.logotopanim);
        walletHubTv.startAnimation(walletHubAnim);
        taglineTv.startAnimation(taglineAnim);
        logoImage.startAnimation(logoImgAnim);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        };


        handler.postDelayed(runnable,3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
