package com.example.plmgapk;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextResgisterFullName, editTextResgisterEmail, editTextResgisterDoB, editTextResgisterMobile,
            editTextResgisterPwd, editTextResgisterConfirmPwd;
    private ImageView imageViewDatePicker;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private static final String TAG= "RegisterActivity";
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toast.makeText(RegisterActivity.this,"You can register now!", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);

        //image icon calendar picker
        imageViewDatePicker = findViewById(R.id.imageView_date_picker);
        editTextResgisterFullName = findViewById(R.id.editText_register_full_name);
        editTextResgisterEmail = findViewById(R.id.editText_register_email);
        editTextResgisterDoB = findViewById(R.id.editText_register_dob);
        editTextResgisterMobile = findViewById(R.id.editText_register_mobile);
        editTextResgisterPwd = findViewById(R.id.editText_register_password);
        editTextResgisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        //radio for gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //date picker
        editTextResgisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Date Picker
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Set the selected date to the TextView or EditText here
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        editTextResgisterDoB.setText(selectedDate);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFullName = editTextResgisterFullName.getText().toString();
                String textEmail = editTextResgisterEmail.getText().toString();
                String textDob = editTextResgisterDoB.getText().toString();
                String textMobile = editTextResgisterMobile.getText().toString();
                String textPwd = editTextResgisterPwd.getText().toString();
                String textConfirmPwd = editTextResgisterConfirmPwd.getText().toString();
                String textGender;

                //Validate num
                String mobileRegex = "^(09|\\+639)\\d{9}$";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);


                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Full Name",
                            Toast.LENGTH_LONG).show();
                    editTextResgisterFullName.setError("Full Name is required");
                    editTextResgisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Email!", Toast.LENGTH_LONG).show();
                    editTextResgisterEmail.setError("Email is required!");
                    editTextResgisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Email!", Toast.LENGTH_LONG).show();
                    editTextResgisterEmail.setError("Valid Email is required!");
                    editTextResgisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDob)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Date of Birth!", Toast.LENGTH_LONG).show();
                    editTextResgisterDoB.setError("Date of Birth is required!");
                    editTextResgisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your Gender!", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Mobile number", Toast.LENGTH_LONG).show();
                    editTextResgisterMobile.setError("Mobile number is required");
                    editTextResgisterMobile.requestFocus();
                } else if (textMobile.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Mobile number", Toast.LENGTH_LONG).show();
                    editTextResgisterMobile.setError("Mobile number should be 12 digits");
                    editTextResgisterMobile.requestFocus();
                } else if (!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Mobile number", Toast.LENGTH_LONG).show();
                    editTextResgisterMobile.setError("Mobile number is not valid!");
                    editTextResgisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    editTextResgisterPwd.setError("Password is required");
                    editTextResgisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Password", Toast.LENGTH_LONG).show();
                    editTextResgisterPwd.setError("Password is Weak");
                    editTextResgisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your Password", Toast.LENGTH_LONG).show();
                    editTextResgisterConfirmPwd.setError("Confirm Password is required");
                    editTextResgisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Password not match", Toast.LENGTH_LONG).show();
                    editTextResgisterConfirmPwd.setError("Confirm Password is required");
                    editTextResgisterConfirmPwd.requestFocus();
                    //delete ang pass
                    editTextResgisterPwd.clearComposingText();
                    editTextResgisterConfirmPwd.clearComposingText();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDob, textGender, textMobile, textPwd);
                    
                }

            }
        });
    }

    //register sa firebase
    private void registerUser(String textFullName, String textEmail, String textDob, String textGender, String textMobile, String textPwd) {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){

                FirebaseUser firebaseUser = auth.getCurrentUser();

                //update
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                firebaseUser.updateProfile(profileChangeRequest);

                //enter user data in firebase
                ReadwriteUserDetails writeUserDetails = new ReadwriteUserDetails(textDob, textGender, textMobile);

                //extract
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            //will send email verify
                            firebaseUser.sendEmailVerification();

                            Toast.makeText(RegisterActivity.this, "User Registered successfully. Please verify your email!", Toast.LENGTH_SHORT).show();

                            //will open profile
                            Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                            //prevent user to go back in register activity by pressing back button
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish(); //close na
                        } else {
                            Toast.makeText(RegisterActivity.this, "User Registered failed. Please try again!", Toast.LENGTH_SHORT).show();

                        }//if done or failed
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    editTextResgisterPwd.setError("Your Password is Weak. Kindly use a mix of alphabets, numbers, and special characters");
                    editTextResgisterPwd.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    editTextResgisterPwd.setError("Your email is invalid or already in use. Kindly use another email!");
                    editTextResgisterPwd.requestFocus();
                } catch (FirebaseAuthUserCollisionException e) {
                    editTextResgisterPwd.setError("User is already have account!");
                    editTextResgisterPwd.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }//if done or failed
                progressBar.setVisibility(View.GONE);
            }
        }
    });
    }
}