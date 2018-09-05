/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.zkcdev.gymbuddy;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.parse.ParseAnalytics;
//import com.parse.starter.R;


public class MainActivity extends AppCompatActivity {


  public void signUp(View view){

      Intent intent = new Intent(getApplicationContext(), signUpActivity.class);
      startActivity(intent);

  }

  public void login(View view){

      Intent intent = new Intent(getApplicationContext(), loginActivity.class);
      startActivity(intent);

  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    
    ParseAnalytics.trackAppOpenedInBackground(getIntent());

  }

}