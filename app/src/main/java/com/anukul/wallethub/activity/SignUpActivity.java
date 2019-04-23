package com.anukul.wallethub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.AppConstant;
import com.anukul.wallethub.R;
import com.anukul.wallethub.model.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userNameEd;
    private EditText userPhoneNoEd;
    private EditText userEmailEd;
    private EditText userPasswordEd;
    private Button signUpBtn;
    private TextView loginTv;

    private ImageView profileImgView;
    private Uri filePath;

    private FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        initView();
    }

    private void initView() {
        userNameEd = findViewById(R.id.activity_signUp_userNameEd);
        userEmailEd = findViewById(R.id.activity_signUp_emailEd);
        userPhoneNoEd = findViewById(R.id.activity_signUp_phoneEd);
        userPasswordEd = findViewById(R.id.activity_signUp_passwordEd);
        profileImgView = findViewById(R.id.activity_signUp_profileIcon);

        signUpBtn = findViewById(R.id.activity_signUp_signUpBtn);
        loginTv = findViewById(R.id.activity_signUp_signInTv);

        progressDialog = new ProgressDialog(SignUpActivity.this);

        profileImgView.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        loginTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_signUp_signUpBtn:
                userRegistration();
                break;
            case R.id.activity_signUp_signInTv:
                finish();
                break;
            case R.id.activity_signUp_profileIcon:
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
                profileImgView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void userRegistration() {
        progressDialog.setTitle("SignUp");
        progressDialog.setMessage("User SignUp Processing...");
        progressDialog.show();

        UserModel userModel = new UserModel();
        final String userName = userNameEd.getText().toString().trim();
        final String userPhoneNo = userPhoneNoEd.getText().toString().trim();
        final String userEmail = userEmailEd.getText().toString().trim();
        final String userPassword = userPasswordEd.getText().toString().trim();

        Glide.with(getApplicationContext())
                .load(userModel.getProfileUrl())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_account_circle_black)
                .into(profileImgView);

        if(userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty()
                || userPassword.isEmpty()){
            Toast.makeText(this, "Please Enter the Details", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else {

            if(filePath != null){
                firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    uploadImage();
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                progressDialog.dismiss();
                Toast.makeText(this, "Please Selecte Profile PIC", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void uploadImage() {
        final String userName = userNameEd.getText().toString().trim();
        final String userPhoneNo = userPhoneNoEd.getText().toString().trim();
        final String userEmail = userEmailEd.getText().toString().trim();
        final String userPassword = userPasswordEd.getText().toString().trim();

        if(filePath != null){
            final StorageReference sRef = storageReference.child("profilePics/" + UUID.randomUUID().toString());
            sRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            insertData(userName,userPhoneNo,userEmail,userPassword,uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "Please Select profile Pic", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    private void insertData(String userName,String userPhoneNo, String userEmail, String userPassword, String imgUrl) {
        databaseReference.child(AppConstant.FIREBASE_NODE_USERS)
                .child(firebaseAuth.getUid())
                .setValue(new UserModel(userName,userPhoneNo,userEmail,userPassword,imgUrl),
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Toast.makeText(SignUpActivity.this, ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }else {
                                    progressDialog.setMessage("Data inserted...wait");
                                    Toast.makeText(SignUpActivity.this, "Data Inserted in Database", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent gotoLoginActivty = new Intent(SignUpActivity.this,SignInActivity.class);
                                    startActivity(gotoLoginActivty);
                                    finish();
                                }
                            }
                        });
    }

}
