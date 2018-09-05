package com.zkcdev.gymbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
//import com.parse.starter.R;

public class loginActivity extends AppCompatActivity {

    public void nextPage(View view){

        EditText loginUsernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
        final EditText loginPasswordEditText = (EditText) findViewById(R.id.loginPasswordEditText);

        ParseUser.logInInBackground(loginUsernameEditText.getText().toString(), loginPasswordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if(e == null){

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);

                }else{

                    Toast.makeText(loginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login Page");

    }
}
