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
import com.madhu.deltachat.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout email, password;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getSupportActionBar().setTitle("Login");

        mAuth=FirebaseAuth.getInstance();

        email=findViewById(R.id.email_login);
        password=findViewById(R.id.password_Login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button loginButton;
        loginButton=findViewById(R.id.login_activity_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=email.getEditText().getText().toString();
                String Password=password.getEditText().getText().toString();
                progressDialog=new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle(R.string.app_name);
                progressDialog.setMessage("Loging in...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


                if (!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password))
                {
                    progressDialog.show();
                    loginUser(Email, Password);
                }
                if (TextUtils.isEmpty(Email))
                    email.setError("Email should not be empty");

                if (TextUtils.isEmpty(Password))
                    password.setError("Pass should not be empty");



            }
        });




    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Intent mainActivity=new Intent(LoginActivity.this, MainActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainActivity);
                    finish();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error Occured\n"+task.getException().getCause()+"\n"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
