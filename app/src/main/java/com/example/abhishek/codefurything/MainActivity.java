package com.example.abhishek.codefurything;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "APPTHING";
    public static final String BASEURL = "http://192.168.42.113:5000";

    private Button configureICEContacts;
    private Button viewICEContacts ;
    private String userKey;
    private FusedLocationProviderClient locationProviderClient;
    private Location userLocation;
    private LocationCallback locationCallback;
    private CountDownTimer countDownTimer;
    private ICEContactsModel database ;
    ArrayList<Contact> iceContactsFromDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new ICEContactsModel( this ) ;
        iceContactsFromDatabase = new ArrayList<Contact>() ;

        configureICEContacts = findViewById( R.id.configure ) ;
        configureICEContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext() , LoadContactsActivity.class )) ;
                Log.d( TAG , "configure ice called " ) ;
            }
        });

        viewICEContacts = findViewById( R.id.view_ice_contacts ) ;
        viewICEContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext() , DisplayICEContacts.class ) ) ;
                Log.d( TAG , "view ice");
            }
        });

        Log.d(TAG, "Main activity started.");

        SharedPreferences preferences = this.getSharedPreferences(MainActivity.TAG, Context.MODE_PRIVATE);
        userKey = preferences.getString("USERKEY", null);

        if (userKey == null) {
            Log.d(TAG, "User not found.");
            Intent registerIntent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(registerIntent);
            finish();
            return;
        }

        if (!locationPermissionGranted() || !contactPermissionGranted()) {
            requestPermissions();
        }

        if (!locationPermissionGranted() || !contactPermissionGranted()) {
            Toast.makeText(getApplicationContext(), "Permissions not granted", Toast.LENGTH_LONG).show();
            return;
        }

        boolean sosSignal = getIntent().getBooleanExtra("SOS", false);
        if (sosSignal) {
            Button button = findViewById(R.id.cancel_action);
            button.setVisibility(View.VISIBLE);
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationProviderClient != null) {
            locationProviderClient.removeLocationUpdates(locationCallback);
        }

        try {
            updateLocation(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_action:

                // run when timer is running.
                if (locationProviderClient == null) {
                    Log.d(TAG, "SOS cancelled!");
                    Toast.makeText(getApplicationContext(), "SOS cancelled", Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();

                    break;
                }

                // run after sos has started.
                Log.d(TAG, "Removed location updates!");
                Toast.makeText(getApplicationContext(), "Sos cancelled", Toast.LENGTH_SHORT).show();
                locationProviderClient.removeLocationUpdates(locationCallback);
                try {
                    updateLocation(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void startTimer() {
        final TextView timerTextView = findViewById(R.id.temp);
        countDownTimer = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Starting SOS", Toast.LENGTH_SHORT).show();
                startSOS();
            }
        };
        countDownTimer.start();
    }

    @SuppressLint("MissingPermission")
    private void startSOS() {
        locationProviderClient = new FusedLocationProviderClient(this);
        locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    userLocation = location;
                    Log.d(TAG, "Last known Location: " + userLocation.toString());
                }

                sendMessages();
            }
        });

        startLocationTracking();
    }

    private void sendMessages() {
        // TODO: user location contains coordinates, update db and send.
        try {
            Toast.makeText(getApplicationContext(), userLocation.toString(), Toast.LENGTH_LONG).show();
            updateLocation(false);
            String url = BASEURL + "/user/location?key=" + userKey;
            Log.d(TAG, "Sent message to ---" + url);

            retrieveContacts( url );

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void retrieveContacts( String url ) throws JSONException {
        iceContactsFromDatabase = database.getAllContacts() ;
        for ( int i = 0 ; i < iceContactsFromDatabase.size() ; i++ ) {
            sendIndividualMessage(  iceContactsFromDatabase.get(i).getName() , iceContactsFromDatabase.get(i).getPhoneNumber()  , url );
        }
    }

    public void sendIndividualMessage ( String name , String phone , String url ) {

            SmsManager smsManager = SmsManager.getDefault() ;
            String message = "HEY : " + name + " !!! " + "\n" + "I AM IN BIG TROUBLE !!! " ;
            message += " MY LOCATION CURRENTLY : " + url ;
            smsManager.sendTextMessage( phone , null , message , null , null );

    }

    @SuppressLint("MissingPermission")
    private void startLocationTracking() {
        Log.d(TAG, "Placing a tracker.");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(120000);        // every 2 mins.
        locationRequest.setFastestInterval(120000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    return;
                }

                userLocation = locationResult.getLastLocation();
                Log.d(TAG, "New location: " + userLocation.toString());

                try {
                    updateLocation(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocation(boolean removeLocation) throws JSONException {
        Log.d(TAG, "Updating location on db.");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = MainActivity.BASEURL + "/api/user/track";

        JSONObject payload = new JSONObject();
        payload.put("key", userKey);

        if (!removeLocation && userLocation != null) {
            payload.put("latitude", userLocation.getLatitude());
            payload.put("longitude", userLocation.getLongitude());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MainActivity.TAG, response.toString());
                    }
                }, null);

        queue.add(jsonObjectRequest);
    }

    private boolean contactPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean locationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                    Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SEND_SMS
                },1);
    }
}
