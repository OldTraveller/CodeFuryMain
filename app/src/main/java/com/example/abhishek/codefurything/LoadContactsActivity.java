package com.example.abhishek.codefurything;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import static android.Manifest.permission.READ_CONTACTS;
import java.util.ArrayList;
import java.util.Arrays;

public class LoadContactsActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 444;
    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    ArrayList<String> contactList;
    Cursor cursor;
    int counter;
    ArrayList<Contact> allContacts;
    ArrayList<Contact> emergencyContacts ;
    boolean ticked[] ;
    Button submit ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_contacts);

        pDialog = new ProgressDialog(LoadContactsActivity.this) ;
        pDialog.setMessage("Reading Contacts ..." ) ;
        pDialog.setCancelable(false);
        pDialog.show();

        mListView = findViewById(R.id.list) ;
        submit = findViewById( R.id.submit ) ;

        allContacts = new ArrayList<Contact>() ;
        emergencyContacts = new ArrayList<Contact>();

        updateBarHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts() ;
            }
        }).start();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTheNumbers() ;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ticked[position] = !ticked[position] ;
            }
        });
    }

    public void sendTheNumbers() {
        for ( int i = 0 ; i < allContacts.size() ; i++ ) {
            if ( ticked[i] ) {
                emergencyContacts.add( allContacts.get(i) ) ;
            }
        }

        Intent intent = new Intent( getApplicationContext() , SendToSave.class ) ;
        intent.putExtra( "emergency_contacts" , emergencyContacts ) ;
        startActivity( intent ) ;

    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }
    public void getContacts() {
        if (!mayRequestContacts()) {
            return;
        }
        contactList = new ArrayList<String>();
        StringBuffer output;
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                output = new StringBuffer();
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });

                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER ) ) ;
                output.append( "NAME   :   " + contactName + "\n" ) ;
                output.append( "PHONE :   " + phoneNumber ) ;
                allContacts.add( new Contact(contactName,phoneNumber) ) ;
                contactList.add(output.toString());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.text1, contactList);
                    mListView.setAdapter(adapter);
                }
            });

            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }
        ticked = new boolean[allContacts.size()] ;
        Arrays.fill( ticked , false );
    }
}


