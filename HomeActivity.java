package com.zkcdev.gymbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseUser;
//import com.parse.starter.R;

public class HomeActivity extends AppCompatActivity {






   // public void request(View view){

        //Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
       // startActivity(intent);

    //}

    //public void checkRequests(View view){

        //Intent intent = new Intent(getApplicationContext(), RequestsPageActivity.class);
        //startActivity(intent);

    //}

    //public void checkActiveRequests(View view){

      //  Intent intent = new Intent(getApplicationContext(), MyRequestsActivity.class);
        //startActivity(intent);

    //}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);

        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){

            ParseUser.logOut();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Home Page");

        ImageView requestBuddyImageView = (ImageView) findViewById(R.id.requestImageView);
        ImageView checkRequestsImageView = (ImageView) findViewById(R.id.checkRequests);
        ImageView myRequestsImageView = (ImageView) findViewById(R.id.myRequests);

        requestBuddyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RequestActivity.class);
                startActivity(intent);
            }
        });

        checkRequestsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RequestsPageActivity.class);
                startActivity(intent);
            }
        });

        myRequestsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyRequestsActivity.class);
                startActivity(intent);
            }
        });

    }
}
