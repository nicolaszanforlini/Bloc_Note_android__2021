package com.example.blocnote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "blocNote.db";

    private static final String id_column = "id";
    private static final String text_column = "doc_texte";
    private static final String name_column = "doc_name";
    private static final String table_name = "file";


    public DataBase( @Nullable Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        String database_create = "CREATE TABLE " + table_name + "(" + id_column +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + name_column + " TEXT NOT NULL, "
                + text_column + " TEXT NOT NULL)";
        db.execSQL( database_create );
        Log.i( "database", "create database" );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        String drop = "drop table " + table_name;
        db.execSQL(drop);
        this.onCreate(db);
        Log.i( "database", "upgrade database" );
    }

    // insert database
    public void insertIntoDatabase( Model m ) {
        if( m.getTitleDoc().equals("") ) {
            Log.i("database", "no file name");
        }else{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put( name_column, m.getTitleDoc() );
            values.put( text_column, m.getTextDoc() );
            db.insert( table_name, null, values );
            db.close();
            Log.i( "database", "insert database ok" );
        }
    }

    // read database to generate a list in home page
    public List<String> readDatabaseToGenerateTextViewListInHome() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + name_column + " FROM " + table_name;
        Cursor cursor = db.rawQuery( query, null );
        List<String>l = new ArrayList<>();
        if ( cursor.moveToFirst() ) {
            do {
                int name = cursor.getColumnIndex( name_column );
                String str_name = cursor.getString( name ).toString();
                l.add(str_name);
            } while ( cursor.moveToNext() );
        }
        cursor.close();
        db.close();
        Log.i( "database", "read column name" );
        return l;
    }

    // read database after choose nameFile in home page
    public List<String> readDatabaseWhenChooseAfile( String nameFile ) {
        String _nameFile = '"' + nameFile + '"';
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + table_name + " where " + name_column + " = " + _nameFile;
        Cursor cursor = db.rawQuery( query, null );
        List<String>l = new ArrayList<>();
        if ( cursor.moveToFirst() ) {
            do {
                int name = cursor.getColumnIndex( name_column );
                int text = cursor.getColumnIndex( text_column );
                String str_name = cursor.getString( name ).toString();
                String str_text = cursor.getString( text ).toString();
                l.add( str_name );
                l.add( str_text );
            } while ( cursor.moveToNext() );
        }
        cursor.close();
        db.close();
        Log.i( "database", "read the selected file" );
        return l;
    }

    // delete old file for a new file save when saved document or when checkBox is checked in home page
    public void deleteOldFile( String nameFile ) {
        String _nameFile = '"' + nameFile + '"';
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = name_column + " = " + _nameFile;
        db.delete( table_name, whereClause, null );
        db.close();
        Log.i("database", "delete file");
    }



}
