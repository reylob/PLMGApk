package com.example.plmgapk;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        // Password show hide
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible, hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change the icon to show the password
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else {
                    // If password is hidden, show it
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // Change the icon to hide the password
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //login button
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email!", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required!");
                    editTextLoginEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter your email!", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is required!");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required!");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });

    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    // get the current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    //check email to verify
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "You are log in now!", Toast.LENGTH_SHORT).show();

                        //open User Pro
                    } else {
                        firebaseUser.sendEmailVerification();
                        //signout user
                        authProfile.signOut();
                        showAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exist or is no longer valid. Please register again!");
                        editTextLoginEmail.requestFocus();
                    }  catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextLoginEmail.setError("Invalid credentials. Kindly, check and re-enter!");
                        editTextLoginEmail.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not Verified");
        builder.setMessage("Please verify your email now. You cannot login without email verification");

        //open email apps if user clicks/continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // to email apps new window and not in the app
                startActivity(intent);
            }
        });

        //create alertdialog
        AlertDialog alertDialog = builder.create();

        //show alertdialog
        alertDialog.show();

    }
}