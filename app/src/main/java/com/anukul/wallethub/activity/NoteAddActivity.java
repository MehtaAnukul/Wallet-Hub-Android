package com.anukul.wallethub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.NoteModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NoteAddActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolbarTitleTv;
    private static final String TAG = NoteAddActivity.class.getSimpleName();
    private EditText titleEd;
    private EditText categoryEd;
    private EditText detailsEd;
    private Button insertBtn;

    private String title;
    private String category;
    private String details;

    private Intent intent;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String userId;
    private String pushkey;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        toolbar = findViewById(R.id.toolbar_layout_toolbar);
        toolbarTitleTv = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbarTitleTv.setText("Note");
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
            insertBtn.setText("UPDATE");
            pushkey = intent.getStringExtra(AppConstant.KEY_PUSHKEY_NOTES);
            showUpdateData(pushkey);
        } else {
            insertBtn.setText("ADD");
        }

    }
    private void initView() {
        titleEd = findViewById(R.id.activity_insert_NotesData_titleEd);
        categoryEd = findViewById(R.id.activity_insert_NotesData_categoryEd);
        detailsEd = findViewById(R.id.activity_insert_NotesData_detailsEd);

        progressDialog = new ProgressDialog(NoteAddActivity.this);
        insertBtn = findViewById(R.id.activity_insert_NotesData_insertBtn);
        insertBtn.setOnClickListener(this);
    }

    private void showUpdateData(String pushkey) {
        databaseReference.child(AppConstant.FIREBASE_NODE_NOTE)
                .child(userId)
                .child(pushkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        Log.e("HELLO", dataSnapshot.getRef() + "");
                        NoteModel noteModel = dataSnapshot.getValue(NoteModel.class);
                        Log.e("HELLO", dataSnapshot.getValue() + "");
                        titleEd.setText(noteModel.getTitle());
                        categoryEd.setText(noteModel.getCategory());
                        detailsEd.setText(noteModel.getDetails());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_insert_NotesData_insertBtn:
                if (insertBtn.getText().equals("UPDATE")) {
                    updateNotesData();

                } else if (insertBtn.getText().equals("ADD")) {
                    insertNotesData();
                }
                break;
        }
    }

    private void updateNotesData() {
        progressDialog.setTitle("Update Notes");
        progressDialog.setMessage("Notes Updating...");
        progressDialog.show();
        //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        final String uuid = firebaseAuth.getCurrentUser().getUid();

        title = titleEd.getText().toString().trim();
        category = categoryEd.getText().toString().trim();
        details = detailsEd.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please Enter the Details", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            if (!uuid.isEmpty()) {

                databaseReference
                        .child(AppConstant.FIREBASE_NODE_NOTE)
                        .child(uuid)
                        .child(pushkey)
                        .setValue(new NoteModel(title, category, details)
                                , new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            progressDialog.dismiss();
                                            Toast.makeText(NoteAddActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NoteAddActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }
                                });
            } else {
                Toast.makeText(this, "UUID not found relogin and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertNotesData() {
        progressDialog.setTitle("Notes");
        progressDialog.setMessage("Notes saving...");
        progressDialog.show();
        //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        final String uuid = firebaseAuth.getCurrentUser().getUid();

        title = titleEd.getText().toString().trim();
        category = categoryEd.getText().toString().trim();
        details = detailsEd.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please Enter the Details", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            if (!uuid.isEmpty()) {

                databaseReference
                        .child(AppConstant.FIREBASE_NODE_NOTE)
                        .child(uuid)
                        .push()
                        .setValue(new NoteModel(title, category, details)
                                , new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            progressDialog.dismiss();
                                            Toast.makeText(NoteAddActivity.this, "Error" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NoteAddActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }
                                });
            } else {
                Toast.makeText(this, "UUID not found relogin and try again", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
