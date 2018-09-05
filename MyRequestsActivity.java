package com.zkcdev.gymbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
//import com.parse.starter.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {


    ListView myRequestListView;
    ArrayList<String> myRequestsArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

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



    public void deleteRequest(View view){

        if(myRequestsArrayList.isEmpty()){
            Toast.makeText(MyRequestsActivity.this, "No requests to delete", Toast.LENGTH_LONG).show();
        }else{
            myRequestsArrayList.clear();
            ParseQuery<ParseObject> requestsToDelete = ParseQuery.getQuery("Requests");
            requestsToDelete.whereContains("username", ParseUser.getCurrentUser().getUsername());

            requestsToDelete.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        if(objects.size() > 0){
                            for(ParseObject deleteRequests: objects){
                                deleteRequests.deleteInBackground();
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

    }

    public void updateMyRequests(){

        myRequestsArrayList.clear();

        final ParseQuery<ParseObject> myRequest = ParseQuery.getQuery("Requests");
        myRequest.whereContains("username", ParseUser.getCurrentUser().getUsername());


        myRequest.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if (objects.size() > 0){
                        for(ParseObject myRequestInfo: objects){

                            final String myLocation = myRequestInfo.getString("gym");
                            final String myTime = myRequestInfo.getString("time");
                            final String myMuscle = myRequestInfo.getString("muscle");
                            final String myExperience = myRequestInfo.getString("experience");

                            Date requestDate = myRequestInfo.getCreatedAt();
                            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
                            String date = dateFormat.format(requestDate);

                            Date currentDate = new Date();
                            String currentDateFormatted = dateFormat.format(currentDate);

                            if(!currentDateFormatted.equals(date)){
                                myRequestInfo.deleteInBackground();
                                //arrayAdapter.notifyDataSetChanged();

                                //myRequestListView.setAdapter(arrayAdapter);

                            }

                            final String info = "Muscle: " + myMuscle + System.lineSeparator() +  System.lineSeparator() + "Time: " + myTime +
                                    System.lineSeparator() + System.lineSeparator() +"Location: " + myLocation + System.lineSeparator() + System.lineSeparator() + "Experience Level: " + myExperience;



                            myRequestsArrayList.add(info);

                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        arrayAdapter = new ArrayAdapter<String>(MyRequestsActivity.this, android.R.layout.simple_list_item_1, myRequestsArrayList);
        myRequestListView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        myRequestListView = (ListView) findViewById(R.id.myRequestsListView);

        setTitle("My Request");
        updateMyRequests();
    }
}
