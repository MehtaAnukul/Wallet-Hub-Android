package com.anukul.wallethub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.LabelModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LabelCreateActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private EditText labelNameEd;
    private Button createLabelBtn;

    private String labelName;
    private Intent intent;
    private String userId;
    private String pushkey;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_create);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Label");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        initView();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        intent = getIntent();
        final boolean status = intent.getBooleanExtra(AppConstant.KEY_CODE, true);

        if (status) {
            createLabelBtn.setText("UPDATE");
            pushkey = intent.getStringExtra(AppConstant.KEY_PUSHKEY_LABEL);
            showUpdateData(pushkey);
        } else {
            createLabelBtn.setText("ADD");
        }

    }
    private void initView() {
        labelNameEd = findViewById(R.id.activtiy_create_label_nameEd);
        createLabelBtn = findViewById(R.id.activity_createLabel_btn);
        createLabelBtn.setOnClickListener(this);
    }
    private void showUpdateData(String pushkey) {
        databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                .child(userId)
                .child(pushkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LabelModel labelModel = dataSnapshot.getValue(LabelModel.class);
                        labelNameEd.setText(labelModel.getLabelName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_createLabel_btn:
                if(createLabelBtn.getText().equals("UPDATE")){
                    updateLabelData();
                }else if(createLabelBtn.getText().equals("ADD")){
                    insertLabelData();
                }
                break;
        }
    }

    private void updateLabelData() {
        final String uuid = firebaseAuth.getCurrentUser().getUid();

        labelName = labelNameEd.getText().toString().trim();
        if(labelName.isEmpty()){
            Toast.makeText(this, "Please Enter the LabelName", Toast.LENGTH_SHORT).show();
        }else {
            if(!uuid.isEmpty()){
                databaseReference.child(AppConstant.FIREBASE_NODE_LABEL)
                        .child(uuid)
                        .child(pushkey)
                        .setValue(new LabelModel(labelName),
                                new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Toast.makeText(LabelCreateActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LabelCreateActivity.this, "Label Updated", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
            }else {
                Toast.makeText(this, "UUID not found relogin and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertLabelData() {
        final String uuid = firebaseAuth.getCurrentUser().getUid();
        final String labelName = labelNameEd.getText().toString().trim();

        if(labelName.isEmpty()){
            Toast.makeText(this, "Please Enter the LabelName", Toast.LENGTH_SHORT).show();
        }else{
            if (!uuid.isEmpty()) {
                    databaseReference
                            .child(AppConstant.FIREBASE_NODE_LABEL)
                            .child(uuid)
                            .push()
                            .setValue(new LabelModel(labelName),
                                    new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    if (databaseError != null) {
                                        Toast.makeText(LabelCreateActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LabelCreateActivity.this, "Label Inserted", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });

            }else{
                Toast.makeText(this, "UUID not found relogin and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
