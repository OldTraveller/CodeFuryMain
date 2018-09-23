package com.example.abhishek.codefurything;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Log.d(MainActivity.TAG, "Registration Activity started");

        Button registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRegistration();
            }
        });
    }

    private void processRegistration() {
        EditText nameField = findViewById(R.id.name_field);
        EditText numberField = findViewById(R.id.number_field);

        String name = nameField.getText().toString();
        String number = numberField.getText().toString();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "One or more field is empty!", Toast.LENGTH_SHORT).show();
            return ;
        }

        try {
            registerUser(name, number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerUser(String name, String number) throws JSONException {
        Log.d(MainActivity.TAG, "Registration process started.");

        final SharedPreferences preferences;
        preferences = this.getSharedPreferences(MainActivity.TAG, Context.MODE_PRIVATE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = MainActivity.BASEURL + "/api/user";

        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("number", number);
        Log.d(MainActivity.TAG , url ) ;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MainActivity.TAG, response.toString());
                        try {
                            String key = response.get("key").toString();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("USERKEY", key);
                            editor.apply();

                            Log.d(MainActivity.TAG, "Registered, starting main activity.");
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, null);

        queue.add(jsonObjectRequest);
    }
}
