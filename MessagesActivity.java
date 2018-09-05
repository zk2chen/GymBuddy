package com.zkcdev.gymbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
//import com.parse.starter.R;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    String userChattingWith = "";

    ArrayList<String> messages = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);

        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout) {

            ParseUser.logOut();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view){

        EditText messageEditText = (EditText) findViewById(R.id.messageEditText);

        ParseObject message = new ParseObject("Message");

        final String messageWrote = messageEditText.getText().toString();

        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("receiver", userChattingWith);
        message.put("messages", messageWrote);

        messageEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    messages.add(messageWrote);
                    arrayAdapter.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();

        String title = intent.getStringExtra("username");

        userChattingWith = title.split(" ")[1];

        setTitle("Messaging " + userChattingWith);

        final ListView messageListView = (ListView) findViewById(R.id.messageListView);

        arrayAdapter = new ArrayAdapter<>(MessagesActivity.this, android.R.layout.simple_list_item_1, messages);


        messageListView.setAdapter(arrayAdapter);


        ParseQuery<ParseObject> youAreSender = ParseQuery.getQuery("Message");
        ParseQuery<ParseObject> theyAreSender = ParseQuery.getQuery("Message");

        youAreSender.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername() );
        youAreSender.whereEqualTo("receiver", userChattingWith);

        theyAreSender.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        theyAreSender.whereEqualTo("sender", userChattingWith);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        queries.add(youAreSender);
        queries.add(theyAreSender);

        ParseQuery<ParseObject> requestQuery = ParseQuery.or(queries);

        requestQuery.orderByAscending("createdAt");

        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0 ){

                        messages.clear();

                        for(ParseObject chats: objects){

                            String chatContent = chats.getString("messages");

                            Date date = chats.getCreatedAt();

                            DateFormat dateTime = new SimpleDateFormat("h:mm a");

                            String messageDate = dateTime.format(date);

                            if(!chats.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                                chatContent = userChattingWith + ": " + chatContent + System.lineSeparator() + System.lineSeparator() +
                                          messageDate + System.lineSeparator();

                            }else{
                                chatContent = ParseUser.getCurrentUser().getUsername() + ": " + chatContent + System.lineSeparator()
                                +System.lineSeparator() + messageDate + System.lineSeparator();
                            }
                            messages.add(chatContent);

                        }

                        arrayAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

    }
}
