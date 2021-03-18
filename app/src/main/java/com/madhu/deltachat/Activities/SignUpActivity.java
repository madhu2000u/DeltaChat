package com.madhu.deltachat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madhu.deltachat.Constants.Constants;
import com.madhu.deltachat.R;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout displayName, e_mail, password_editText;
    private Button signUPButton;
    ProgressDialog progressDialog;
    private DatabaseReference database;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        getSupportActionBar().setTitle("Create Account");

        mAuth = FirebaseAuth.getInstance();

        displayName = findViewById(R.id.display_nameSignUp);
        e_mail = findViewById(R.id.enter_emailSignUP);
        password_editText = findViewById(R.id.enter_passwordSignUp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*displayName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d("msg","I am in onFocusChange()");
                if (view.isFocused())
                    displayName.setBoxBackgroundColorResource(R.color.colorAccent);
            }
        });*/


        signUPButton = findViewById(R.id.sign_up_activity_button);

        signUPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Display_name = displayName.getEditText().getText().toString();
                String Email = e_mail.getEditText().getText().toString();
                String Password = password_editText.getEditText().getText().toString();
                progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setTitle(R.string.app_name);
                progressDialog.setMessage("Signing up...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


                if (!TextUtils.isEmpty(Display_name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password)) {
                    progressDialog.show();
                    registerUser(Display_name, Email, Password);
                }
                if (TextUtils.isEmpty(Display_name))
                    displayName.setError("Display name should not be empty");
                if (TextUtils.isEmpty(Email))
                    e_mail.setError("Email should not be empty");
                if (TextUtils.isEmpty(Password))
                    password_editText.setError("Pass should not be empty");


            }
        });


    }

    private void registerUser(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String UID = currentUser.getUid();


                    database = FirebaseDatabase.getInstance().getReference().child(Constants.databaseRoot).child(UID);

                    HashMap<String, String> userHashMap = new HashMap<>();

                    userHashMap.put("Uid", UID);
                    userHashMap.put("name", display_name);
                    userHashMap.put("status", "Hi There, I am using Delta Chat.");
                    userHashMap.put("image", "default");
                    userHashMap.put("thumbNail", "default");
                    progressDialog.show();

                    database.setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Intent mainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainActivity);
                                finish();

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Registration failed. Check you Internet connection or try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                } else {
                    progressDialog.dismiss();

                    Toast.makeText(SignUpActivity.this, "Error Occured\n" + task.getException().getCause() + "\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
