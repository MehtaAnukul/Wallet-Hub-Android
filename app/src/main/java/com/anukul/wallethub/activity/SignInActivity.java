package com.anukul.wallethub.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anukul.wallethub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText loginEmailEd;
    private EditText loginPasswordEd;
    private Button loginBtn;
    private TextView signUpTv;
    private TextView forgotPasswordTv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) {
            final Intent gotoHome = new Intent(SignInActivity.this,
                    HomeActivity.class);
            startActivity(gotoHome);
            finish();
        }

        initView();
    }

    private void initView() {
        loginEmailEd = findViewById(R.id.activity_signIn_emailEd);
        loginPasswordEd = findViewById(R.id.activity_signIn_passwordEd);
        loginBtn = findViewById(R.id.activity_signIn_signInBtn);
        signUpTv = findViewById(R.id.activity_signIn_signUpTv);
        forgotPasswordTv = findViewById(R.id.activity_signIn_forgotPasswordTv);

        progressDialog = new ProgressDialog(SignInActivity.this);

        loginBtn.setOnClickListener(this);
        signUpTv.setOnClickListener(this);
        forgotPasswordTv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_signIn_signInBtn:
                userLogin();
                break;
            case R.id.activity_signIn_signUpTv:
                Intent gotoSignUpActivity = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(gotoSignUpActivity);
                break;
            case R.id.activity_signIn_forgotPasswordTv:
                forgotPassword();
        }
    }

    private void forgotPassword() {

        final String loginEmail = loginEmailEd.getText().toString().trim();

        if (loginEmail.isEmpty()) {
            Toast.makeText(this, "Email ID not Entered", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(loginEmail)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(SignInActivity.this, "Password send In Email", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void userLogin() {
        progressDialog.setTitle("SignIn");
        progressDialog.setMessage("User SignIn Processing...");
        progressDialog.show();
        final String loginEmail = loginEmailEd.getText().toString().trim();
        final String loginPassword = loginPasswordEd.getText().toString().trim();

        if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Enter datails", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Login SuccessFully Done", Toast.LENGTH_SHORT).show();

                                Intent gotoHomeActivity = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(gotoHomeActivity);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
