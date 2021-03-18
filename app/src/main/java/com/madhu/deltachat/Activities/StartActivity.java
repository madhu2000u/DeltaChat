package com.madhu.deltachat.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.madhu.deltachat.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private Button signUpButton;
    private Button loginButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        signUpButton=findViewById(R.id.sign_up_button);
        loginButton=findViewById(R.id.sign_in_button);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            signUpButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);











    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.sign_up_button:
                //intentStarter(SignUpActivity.class);
                Log.d("msg","signUp Button clicked");
                Intent signUpIntent=new Intent(this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;

            case R.id.sign_in_button:
                Log.d("msg","login Button clicked");
                //intentStarter(LoginActivity.class);
                Intent loginIntent=new Intent(this, LoginActivity.class);
                startActivity(loginIntent);

                break;
        }

    }


}

