package com.anukul.wallethub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText userNameEd;
    private EditText userPhoneEd;
    private TextView userEmailTv;
    private TextView userNameTv;
    private Button updateBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReferencel;
    private FirebaseDatabase firebaseDatabase;

    private String userId;
    private String phoneNo;
    private String email;
    private String password;
    private String profileUrl;

    private FloatingActionButton cameraFloatingActionbtn;
    private ImageView profileImg;
    private Uri filePath;
    FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private LinearLayout photoLinearLayout;
    private LinearLayout labelLinearLayout;
    private LinearLayout videoLinearLayout;

    private TextView photo_count;
    private TextView label_count;
    private TextView video_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        /*toolbar = findViewById(R.id.toolbar_layout_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencel = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        initView();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Toast.makeText(UpdateProfileActivity.this, "Successfully signed "+user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    // Toast.makeText(UpdateProfileActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                }
            }
        };

        databaseReferencel.child(AppConstant.FIREBASE_NODE_USERS).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void showData(DataSnapshot dataSnapshot) {
        UserModel userModel = dataSnapshot.getValue(UserModel.class);

        profileUrl = userModel.getProfileUrl();
        password = userModel.getUserPassword();
        phoneNo = userModel.getUserPhoneNo();
        email = userModel.getUserEmail();

        userNameEd.setText(userModel.getUserName());
        userPhoneEd.setText(userModel.getUserPhoneNo());
        userEmailTv.setText(userModel.getUserEmail());
        userNameTv.setText(userModel.getUserName());

        Glide.with(getApplicationContext())
                .load(userModel.getProfileUrl())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_account_circle_black)
                .into(profileImg);
    }

    private void initView() {
        cameraFloatingActionbtn = findViewById(R.id.activity_profile_floatingActionBtn);
        profileImg = findViewById(R.id.activity_profile_profileIcon);
        userNameEd = findViewById(R.id.activity_profile_userNameEd);
        userPhoneEd = findViewById(R.id.activity_profile_phoneEd);
        userEmailTv = findViewById(R.id.activity_profile_emailTv);
        userNameTv = findViewById(R.id.activity_profile_userNameTv);
        updateBtn = findViewById(R.id.activity_profile_updateBtn);

        photoLinearLayout = findViewById(R.id.linearlayout_photo);
        videoLinearLayout = findViewById(R.id.linearlayout_video);
        labelLinearLayout = findViewById(R.id.linearlayout_label);

        photo_count = findViewById(R.id.photo_count);
        video_count = findViewById(R.id.video_count);
        label_count = findViewById(R.id.label_count);


        progressDialog = new ProgressDialog(ProfileActivity.this);
        cameraFloatingActionbtn.setOnClickListener(this);
        // profileImg.setOnClickListener(this);
        updateBtn.setOnClickListener(this);

        databaseReferencel
                .child(AppConstant.FIREBASE_NODE_IMAGE)
                .child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        photo_count.setText(dataSnapshot.getChildrenCount() + "");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReferencel
                .child(AppConstant.FIREBASE_NODE_LABEL)
                .child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        label_count.setText(dataSnapshot.getChildrenCount() + "");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReferencel
                .child(AppConstant.FIREBASE_NODE_VIDEO)
                .child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        video_count.setText(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


        photoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(ProfileActivity.this, ImageViewActivity.class);
                startActivity(intent);
            }
        });

        labelLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ProfileActivity.this, LabelViewActivity.class);
                startActivity(intent);
            }
        });

        videoLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ProfileActivity.this, VideoViewActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_profile_updateBtn:
                updateData();
                break;
            case R.id.activity_profile_floatingActionBtn:
                chooseImage();
                break;
        }

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppConstant.KEY_PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.KEY_PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateData() {
        final String userName = userNameEd.getText().toString().trim();
        final String userPhone = userPhoneEd.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            userNameEd.setError("Please Enter UserName");
        } else if (TextUtils.isEmpty(userPhone)) {
            userPhoneEd.setError("Please Enter PhoneNo");
        } else {
            uploadDataImg();
        }
    }

    private void uploadDataImg() {
        final String userName = userNameEd.getText().toString().trim();
        final String userPhone = userPhoneEd.getText().toString().trim();

        progressDialog.setMessage("Update in Progress");
        progressDialog.show();

        if (filePath != null) {
            final StorageReference sRef = storageReference.child("profilePics/" + UUID.randomUUID().toString());
            sRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            insertData(userName, userPhone, uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            insertData(userName, userPhone, profileUrl);
        }
    }

    private void insertData(String userName, String userPhone, String imgUrl) {
        final String uuid = firebaseAuth.getCurrentUser().getUid();
        if (!uuid.isEmpty()) {
            databaseReferencel.child(AppConstant.FIREBASE_NODE_USERS)
                    .child(userId).setValue(new UserModel(userName, userPhone, email, password, imgUrl),
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

        }
    }

    /*final String uuid = firebaseAuth.getCurrentUser().getUid();

        if (!uuid.isEmpty()) {

        databaseReferencel
                .child(AppConstant.FIREBASE_NODE_USERS)
                .child(uuid)
                .child("userName")
                .setValue(userNameEd.getText().toString(),
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                if (databaseError != null) {
                                    Toast.makeText(ProfileActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Update Username", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            }
                        });


    }*/
}


