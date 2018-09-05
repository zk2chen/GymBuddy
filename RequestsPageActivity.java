package com.zkcdev.gymbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class RequestsPageActivity extends AppCompatActivity {

    ListView requestListView;
    ArrayList<String> requests = new ArrayList<String>();
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

    public void updateRequests(){

        requests.clear();


        final ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("Requests");
        requestQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        ParseQuery<ParseObject> userGymLocation = ParseQuery.getQuery("Requests").whereContains("username", ParseUser.getCurrentUser().getUsername());


        userGymLocation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject userRequestInfo: objects){
                            final String userLocation = (String) userRequestInfo.get("gym");
                            final String userMuscle = (String) userRequestInfo.get("muscle");
                            final String userTime = (String) userRequestInfo.get("time");
                            final String userExperience = (String) userRequestInfo.get("experience");

                            requestQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e == null){
                                        if(objects.size() > 0){

                                            for (ParseObject object: objects) {

                                                String muscleGroup = (String) object.get("muscle");
                                                String location = (String) object.get("gym");
                                                String time = (String) object.get("time");
                                                String experience = (String) object.get("experience");
                                                String userName = object.getString("username");
                                                Date requestDate = object.getCreatedAt();
                                                DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
                                                String date = dateFormat.format(requestDate);

                                                Date currentDate = new Date();
                                                String currentDateFormatted = dateFormat.format(currentDate);


                                                if (!currentDateFormatted.equals(date)){
                                                    object.deleteInBackground();
                                                    //arrayAdapter.notifyDataSetChanged();

                                                    //requestListView.setAdapter(arrayAdapter);
                                                }


                                                //String info = "Muscle: " + muscleGroup + System.lineSeparator() +  System.lineSeparator() + "Time: " + time +
                                                        //System.lineSeparator() + System.lineSeparator() +"Location: " + location + System.lineSeparator();
                                                String info = "User: " + userName + " ( Experience: " + experience + ")" + System.lineSeparator()
                                                        + "Workout and Time: " + muscleGroup + " at " + time + System.lineSeparator()
                                                        + "Location: " + location + System.lineSeparator() + System.lineSeparator() + "Date: " + date + System.lineSeparator() ;


                                            if(location.equals(userLocation) && experience.equals(userExperience)) {
                                                if(time.equals(userTime) || time.equals("Anytime") || userTime.equals("Anytime")){
                                                    if(muscleGroup.equals(userMuscle) || muscleGroup.equals("Any") || userMuscle.equals("Any")){
                                                            requests.add(info);
                                                    }
                                                }

                                            }
                                            }
                                            arrayAdapter.notifyDataSetChanged();

                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });


        arrayAdapter = new ArrayAdapter<String>(RequestsPageActivity.this, android.R.layout.simple_list_item_1, requests);
        requestListView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_page);

        setTitle("Requests");

        requestListView = (ListView) findViewById(R.id.requestsListView);

        updateRequests();

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);

                intent.putExtra("username", requests.get(position));

                startActivity(intent);

            }
        });

    }
}
