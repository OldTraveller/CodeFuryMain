package com.example.abhishek.codefurything;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class ICEContactsModel extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "women_safety_app" ;
    public static final String ICE_CONTACTS_TABLE = "ice_contacts" ;
    public static final String ICE_CONTACT_NAME = "ice_contact_name" ;
    public static final String ICE_CONTACT_NUMBER = "ice_contact_number" ;
    public static final String ICE_CONTACT_ID = "ice_contact_id" ;

    ICEContactsModel ( Context context ) {
        super( context , DATABASE_NAME , null , 1 ) ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE IF NOT EXISTS ice_contacts ( ice_contact_id integer PRIMARY KEY AUTOINCREMENT , ice_contact_name text , ice_contact_number text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + ICE_CONTACTS_TABLE  );
        onCreate( db ) ;
    }

    public boolean insertContact (String name, String phone ) {
        SQLiteDatabase db = this.getWritableDatabase();
        int counter = numberOfRows() ;
        Log.d("Value of counter : " , String.valueOf(counter) ) ;
        Log.d("NAME : " , name ) ;

        try {
            ContentValues values = new ContentValues() ;
            values.put( ICE_CONTACT_NAME , name ) ;
            values.put( ICE_CONTACT_NUMBER , phone ) ;
            db.insert( ICE_CONTACTS_TABLE , null , values ) ;
            return true ;
        } catch ( Exception e ) {
            return false ;
        }
    }

    public boolean deleteContact ( int id ) {
        SQLiteDatabase db = this.getWritableDatabase() ;
        String query = "DELETE FROM " + ICE_CONTACTS_TABLE + " WHERE ID = " + id ;

        if ( db.rawQuery( query , null ) != null ) return true ;
        return false ;
    }

    public Cursor getData(int id ) {
        SQLiteDatabase db = this.getReadableDatabase() ;
        String query = "SELECT * FROM " + ICE_CONTACTS_TABLE + " WHERE " + ICE_CONTACT_ID + " = " + id ;
        Cursor cursor = db.rawQuery( query , null ) ;
        return cursor ;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ICE_CONTACTS_TABLE );
        return numRows;
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> iceContacts = new ArrayList<Contact>() ;
        SQLiteDatabase db = this.getReadableDatabase() ;
        String query = "SELECT * FROM " + ICE_CONTACTS_TABLE ;
        Cursor cursor = db.rawQuery( query , null ) ;
        cursor.moveToFirst() ;
        while ( ! cursor.isAfterLast() ) {
            iceContacts.add( new Contact( cursor.getString(cursor.getColumnIndex(ICE_CONTACT_NAME)) , cursor.getString(cursor.getColumnIndex( ICE_CONTACT_NUMBER ))))  ;
            cursor.moveToNext() ;
        }
        return iceContacts ;
    }

}