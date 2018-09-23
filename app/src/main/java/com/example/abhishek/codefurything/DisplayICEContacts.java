package com.example.abhishek.codefurything;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayICEContacts extends AppCompatActivity {

    ListView iceContactsList;
    ICEContactsModel database ;
    ArrayList<String> toDisplayICE ;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_icecontacts);

        iceContactsList = findViewById( R.id.list_ice_contact ) ;
        toDisplayICE = new ArrayList<String>() ;
        database = new ICEContactsModel(this) ;
        retrieveAndDisplay() ;

        // this alert thing is also not working !!! try this

        iceContactsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
                builder.setTitle("Delete").setMessage( "Do you want to delete : " + toDisplayICE.get(position) )
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.deleteContact( position ) ;
                        onCreate(savedInstanceState);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
                return true;
            }
        });
    }

    public void retrieveAndDisplay() {
        ArrayList<Contact> iceContactsFromDB = database.getAllContacts() ;
        Log.d("ICECONTACT " , iceContactsFromDB.size() + " is the size " ) ;
        for ( Contact contact : iceContactsFromDB ) {
            toDisplayICE.add( "Name : " + contact.getName() + "\n" + "Phone " + contact.getPhoneNumber() ) ;
        }

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, R.id.text1, toDisplayICE ) ;
        iceContactsList.setAdapter( adapter ) ;
    }


}
