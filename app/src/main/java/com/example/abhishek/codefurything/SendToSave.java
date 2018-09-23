package com.example.abhishek.codefurything;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SendToSave extends AppCompatActivity {

    ArrayList<Contact> contacts = new ArrayList<Contact>() ;
    private ICEContactsModel database ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contacts = (ArrayList<Contact>) getIntent().getSerializableExtra("emergency_contacts") ;
        database = new ICEContactsModel(this) ;

        StringBuffer nameBuffer = new StringBuffer("") ;
        StringBuffer phoneBuffer = new StringBuffer("") ;

        for ( int i = 0 ; i < contacts.size() ; i++ ) {
            database.insertContact( contacts.get(i).getName() , contacts.get(i).getPhoneNumber() ) ;
            nameBuffer.append( contacts.get(i).getName() ).append("$") ;
            phoneBuffer.append( contacts.get(i).getPhoneNumber()).append("$") ;
        }

        nameBuffer.deleteCharAt( nameBuffer.length() - 1 ) ;
        phoneBuffer.deleteCharAt( phoneBuffer.length() - 1 ) ;
        Log.d(MainActivity.TAG ,nameBuffer.toString()) ;

        try {
            saveContacts( nameBuffer.toString() , phoneBuffer.toString() ) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("The Saved items : " , String.valueOf(database.numberOfRows()) ) ;
        String toast = contacts.size() + " ICE CONTACTS HAVE BEEN SAVED SUCCESSFULLY !! " ;
        Toast.makeText( getApplicationContext() , toast , Toast.LENGTH_SHORT ).show();
        Intent intent = new Intent( getApplicationContext() , MainActivity.class ) ;
        startActivity (intent);
    }


    private void saveContacts(String name, String number) throws JSONException {
        Log.d(MainActivity.TAG, "Save process started.");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = MainActivity.BASEURL + "/api/user/contacts";

        SharedPreferences preferences;
        preferences = this.getSharedPreferences(MainActivity.TAG, Context.MODE_PRIVATE);

        String userKey = preferences.getString("USERKEY", null);
        JSONObject payload = new JSONObject();
        payload.put("key" , userKey ) ;
        payload.put("names", name);
        payload.put("numbers", number);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MainActivity.TAG, response.toString());
                    }
                }, null);

        queue.add(jsonObjectRequest);
    }

}
