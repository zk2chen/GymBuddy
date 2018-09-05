package com.zkcdev.gymbuddy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
//import com.parse.starter.R;
import com.zkcdev.gymbuddy.models.PlaceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    AutoCompleteTextView muscleAutoTextView;
    AutoCompleteTextView timeOfWorkoutAutoTextView;
    AutoCompleteTextView experienceLevel;

    String[] muscleList = {"Any", "Chest", "Legs", "Shoulders", "Arms", "Back"};
    String[] timeList = {"Anytime", "7:00am-7:30am", "7:30am-8:00am", "8:00am-8:30am", "8:30am-9:00am", "9:00am-9:30am", "9:30am-10:00am",
                        "10:00am-10:30am", "10:30am-11:00am", "11:00am-11:30am", "11:30am-12:00pm", "12:00pm-12:30pm", "12:30pm-1:00pm",
                        "1:00pm-1:30pm", "1:30pm-2:00pm", "2:00pm-2:30pm", "2:30pm-3:00pm", "3:00pm-3:30pm", "3:30pm-4:00pm", "4:00pm-4:30pm",
                        "4:30pm-5:00pm", "5:00pm-5:30pm", "5:30pm-6:00pm", "6:00pm-6:30pm", "6:30pm-7:00pm", "7:00pm-7:30pm", "7:30pm-8:00pm",
                        "8:00pm-8:30pm", "8:30pm-9:00pm", "9:00pm-9:30pm", "9:30pm-10:00pm", "10:00pm-10:30pm", "10:30pm-11:00pm",
                        "11:00pm-11:30pm", "11:30pm-12:00am"};

    String[] experienceList = {"Beginner", "Intermediate", "Advanced"};

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private GoogleMap mMap;
    private static final LatLngBounds latlngbounds = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    LocationManager locationManager;
    LocationListener locationListener;

    AutoCompleteTextView gymSearchEditText;
    ImageView returnMyLocation;
    PlaceAutoCompleteAdapter placeAutoCompleteAdapter;

    GoogleApiClient googleApiClient;
    PlaceInfo mPlace;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gymSearchEditText = (AutoCompleteTextView) findViewById(R.id.gymSearch); //
        returnMyLocation = (ImageView) findViewById(R.id.ic_gps);

        ArrayAdapter<String> muscleAdapter = new ArrayAdapter<String>(RequestActivity.this, android.R.layout.select_dialog_singlechoice, muscleList);
        muscleAutoTextView = (AutoCompleteTextView) findViewById(R.id.bodyPart);
        muscleAutoTextView.setThreshold(1);
        muscleAutoTextView.setAdapter(muscleAdapter);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(RequestActivity.this, android.R.layout.select_dialog_singlechoice, timeList);
        timeOfWorkoutAutoTextView = (AutoCompleteTextView) findViewById(R.id.timeOf);
        timeOfWorkoutAutoTextView.setThreshold(1);
        timeOfWorkoutAutoTextView.setAdapter(timeAdapter);

        ArrayAdapter<String> experienceAdapter = new ArrayAdapter<String>(RequestActivity.this, android.R.layout.select_dialog_singlechoice, experienceList);
        experienceLevel = (AutoCompleteTextView) findViewById(R.id.experienceLevel);
        experienceLevel.setThreshold(1);
        experienceLevel.setAdapter(experienceAdapter);

        //init();//new code
    }

    public void hideSoftKeyBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void geoLocate() {

        String searchString = gymSearchEditText.getText().toString();

        Geocoder geocoder = new Geocoder(RequestActivity.this);

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.i("info", e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.i("Address", address.toString());

            LatLng gymLocation = new LatLng(address.getLatitude(), address.getLongitude()); //newer code

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gymLocation, 15)); //newer code

            MarkerOptions options = new MarkerOptions().position(gymLocation).title(null); //newer code

            mMap.addMarker(options);

            hideSoftKeyBoard();
        }


    }//

    public void init() {

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build(); //

        gymSearchEditText.setOnItemClickListener(autoCompleteListener);


        placeAutoCompleteAdapter = new PlaceAutoCompleteAdapter(RequestActivity.this, googleApiClient, latlngbounds, null ); //newest code

        //setting the adapter
        gymSearchEditText.setAdapter(placeAutoCompleteAdapter);

        gymSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

        returnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RequestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                Location userLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLocation = new LatLng(userLastLocation.getLatitude(), userLastLocation.getLongitude());
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }
        });
        hideSoftKeyBoard();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION} , 1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0 , locationListener);
                Location userLastLocation =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLocation = new LatLng(userLastLocation.getLatitude(), userLastLocation.getLongitude());
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
            }
        }
        init();
    }

    /*
    --------------------------google places API autocomplete suggestions--------------------
     */

    public AdapterView.OnItemClickListener autoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            hideSoftKeyBoard();

            AutocompletePrediction item = placeAutoCompleteAdapter.getItem(position);
            String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);

            placeResult.setResultCallback(updatePlaceDetailsCallback);

        }
    };

    public ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.i("places", "failure");
                places.release();
                return;
            }
            Place place = places.get(0);


                mPlace = new PlaceInfo();
                mPlace.setLatLng(place.getLatLng());


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPlace.getLatLng(),15));

            MarkerOptions options = new MarkerOptions().position(mPlace.getLatLng()).title(null); //newer code

            mMap.addMarker(options);

            places.release();

        }
    };

        /*
    --------------------------Request Buddy--------------------
     */


    public void requestBuddy(View view){

        if(muscleAutoTextView.getText().toString().matches("")
            ||timeOfWorkoutAutoTextView.getText().toString().matches("")
            ||gymSearchEditText.getText().toString().matches("")
            ||experienceLevel.getText().toString().matches("")){

            Toast.makeText(RequestActivity.this, "Invalid Request", Toast.LENGTH_LONG).show();
        }else{


            ParseQuery<ParseObject> checkIfAnyRequests = ParseQuery.getQuery("Requests");
            checkIfAnyRequests.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

            checkIfAnyRequests.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        if(objects.isEmpty()){
                            ParseObject request = new ParseObject("Requests");
                            request.put("username", ParseUser.getCurrentUser().getUsername());
                            request.put("gym", gymSearchEditText.getText().toString());
                            request.put("muscle", muscleAutoTextView.getText().toString());
                            request.put("time", timeOfWorkoutAutoTextView.getText().toString());
                            request.put("experience", experienceLevel.getText().toString());

                            request.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        Toast.makeText(RequestActivity.this, "Request Made! Check Requests for any requests", Toast.LENGTH_LONG).show();

                                    }else{
                                        Toast.makeText(RequestActivity.this, "Unavailable to make request at the moment. Try again later", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RequestActivity.this, "Can only have one active request. Delete active request to make new one", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });


        }

    }

            /*
    --------------------------Logout--------------------
     */

    public void logout(View view){

        ParseUser.logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}
