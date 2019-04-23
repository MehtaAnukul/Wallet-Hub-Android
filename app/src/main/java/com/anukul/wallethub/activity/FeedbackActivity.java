package com.anukul.wallethub.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.FeedbackModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private EditText titleEd;
    private EditText messageEd;
    private TextView titleRateTv;
    private TextView resultRateTv;
    private RatingBar rateStars;
    private ImageView smileImg;
    private Button submitBtn;
    private String ratingText;
    String answerValue;
    Animation smileAnim,btnAnim;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferencel;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Feedback");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencel = firebaseDatabase.getReference();

        //load animation
        smileAnim = AnimationUtils.loadAnimation(this,R.anim.charanim);
        btnAnim = AnimationUtils.loadAnimation(this,R.anim.btn);

        initView();
    }

    private void initView() {
        titleEd = findViewById(R.id.activity_feedback_titleEd);
        messageEd = findViewById(R.id.activity_feedback_messageEd);
        titleRateTv = findViewById(R.id.activity_feedback_titleRateTv);
        resultRateTv = findViewById(R.id.activity_feedback_resultRateTv);
        rateStars = findViewById(R.id.activity_feedback_rateStar);
        smileImg = findViewById(R.id.activity_feedback_smileIcon);
        submitBtn = findViewById(R.id.activity_feedback_sbmtBtn);

        rateStars.setOnRatingBarChangeListener(this);
        submitBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_feedback_sbmtBtn:
                submitfeedbackData();
                break;

        }
    }

    private void submitfeedbackData() {
        final String title = titleEd.getText().toString().trim();
        final String message = messageEd.getText().toString().trim();

        final String uuid = firebaseAuth.getCurrentUser().getUid();
        if (!uuid.isEmpty()) {
            databaseReferencel
                    .child(AppConstant.FIREBASE_NODE_FEEDBACK)
                    .child(uuid)
                    .setValue(new FeedbackModel(title, message,ratingText),
                            new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Toast.makeText(FeedbackActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(FeedbackActivity.this, "Feedback Successfully submited...", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        answerValue = String.valueOf((int) (rateStars.getRating()));
        if (answerValue.equals("1")) {
            smileImg.setImageResource(R.drawable.need);
            smileImg.startAnimation(smileAnim);
            submitBtn.startAnimation(btnAnim);
            resultRateTv.setText("Need improvement");
            ratingText = "Need Improvement";

            smileImg.startAnimation(smileAnim);
            submitBtn.startAnimation(btnAnim);


        } else if (answerValue.equals("2")) {
            smileImg.setImageResource(R.drawable.fair);
            smileImg.startAnimation(smileAnim);
            submitBtn.startAnimation(btnAnim);
            resultRateTv.setText("Fair");
            ratingText = "Fair";
        } else if (answerValue.equals("3")) {
            smileImg.setImageResource(R.drawable.good);
            smileImg.startAnimation(smileAnim);
            submitBtn.startAnimation(btnAnim);
            resultRateTv.setText("Good");
            ratingText = "Good";
        } else if (answerValue.equals("4")) {
            smileImg.setImageResource(R.drawable.very_good);
            smileImg.startAnimation(smileAnim);
            submitBtn.startAnimation(btnAnim);
            ratingText = "Very Good";
            resultRateTv.setText("Very Good");
        } else if (answerValue.equals("5")) {
            smileImg.setImageResource(R.drawable.excellent);
            smileImg.startAnimation(smileAnim);
            submitBtn.startAnimation(btnAnim);
            ratingText = "Excellent";
            resultRateTv.setText("Excellent");
        } else {
            Toast.makeText(this, "No Point", Toast.LENGTH_SHORT).show();
        }
    }
}
