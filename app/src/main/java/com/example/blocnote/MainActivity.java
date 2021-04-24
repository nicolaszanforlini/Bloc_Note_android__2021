package com.example.blocnote;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private DataBase db;

    private MaterialButton btnDelete;
    private MaterialButton btnNewDoc;
    private TableLayout tabTxt;
    private List<String> listNameFileInDatabase = new ArrayList<>();
    private List<String>l_file = new ArrayList<>();

    public static String NAME_FILE;
    public static String TEXT_FILE;

    public static boolean activateSelectedFile;

    private List<String>l_if_checked = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabTxt = (TableLayout) findViewById( R.id.tabTxt );
        btnNewDoc = (MaterialButton)findViewById( R.id.btnNewDoc );
        btnNewDoc.setOnClickListener( btnNewDocListener );
        btnDelete = (MaterialButton)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener( btnDeleteListener );

        db = new DataBase(this );

        activateSelectedFile = false;

        // try to create list textView of database column nameFile
        try{
            listNameFileInDatabase = db.readDatabaseToGenerateTextViewListInHome();
            Log.i("infos liste", String.valueOf( listNameFileInDatabase.size() ) );

            int i = 0;
            while ( i != listNameFileInDatabase.size() ) {
                LinearLayout contain = new LinearLayout( this );

                // textView file
                TextView txt = new TextView( this );
                txt.setTag( i );
                txt.setOnClickListener( txtListColumnListener );
                txt.setText( listNameFileInDatabase.get( i ) );
                txt.setTextSize( 20 );

                // checkBox to deleted file
                CheckBox chekDel = new CheckBox(this );
                chekDel.setTag( i );
                chekDel.setOnClickListener( checkDeleteListener );

                // add row checkDelete and Textview
                contain.addView( chekDel );
                contain.addView( txt );
                tabTxt.addView( contain );
                i++;
            }
        }catch( Exception e ) {
            e.printStackTrace();
            Log.i("column nameFile", "empty" );
        }

    }



    // button listener "+"  --> go to second activity
    public View.OnClickListener btnNewDocListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent docIntent = new Intent(MainActivity.this, ControlDocActivity.class );
            startActivity( docIntent );
        }
    };

    // textView listener of database column fileName
    public View.OnClickListener txtListColumnListener = new View.OnClickListener() {
        @SuppressLint("CommitPrefEdits")
        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            l_file = db.readDatabaseWhenChooseAfile( listNameFileInDatabase.get(i).toString() );
            NAME_FILE = l_file.get( 0 ).toString();
            TEXT_FILE = l_file.get( 1 ).toString();
            activateSelectedFile = true;
            Intent intent = new Intent( MainActivity.this, ControlDocActivity.class );
            startActivity( intent );
        }
    };

    // checkBox delete listener
    public View.OnClickListener checkDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            String tmp = listNameFileInDatabase.get( i ).toString();
            if( !l_if_checked.contains( tmp ) ) {
                l_if_checked.add( tmp );
                Log.i("liste", "add to list "+ tmp );
            }else if( l_if_checked.contains( tmp ) ) {
                l_if_checked.remove( tmp );
                Log.i("liste", "del to list "+ tmp );
            }
        }
    };

    // button delete listener
    public View.OnClickListener btnDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( !l_if_checked.isEmpty() ) {

                // alertDialog to comfirm deleting file
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle( "delete" );
                alert.setMessage( "comfirm deleting file" );
                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            for ( int i=0;i<=l_if_checked.size();i++ ) {
                                String tmp = l_if_checked.get( i ).toString();
                                db.deleteOldFile( tmp );
                                Log.i("file", "file deleted " + tmp );
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.i("exception", "out of liste");
                        }
                        reload();
                    }
                })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast toastCancel = Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT);
                                toastCancel.setGravity(Gravity.CENTER, 0, 0);
                                toastCancel.show();
                            }
                        });
                alert.create();
                alert.show();
            }else{
                Log.i("button delete", "not activate" );
            }
        }
    };

    // reload activity
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }



}