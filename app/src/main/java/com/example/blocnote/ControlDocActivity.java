package com.example.blocnote;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;


public class ControlDocActivity extends AppCompatActivity implements View.OnClickListener {

    private DataBase db;

    private MaterialButton btnReturn;
    private MaterialButton btnSave;
    private MaterialButton btnClear;
    private EditText editTxtMultiligne;
//    private CheckBox checkBoxBold;
//    private CheckBox chekBoxItalic;

    private Model modelDoc;
    private String texte;

    // save current text in editTextMultiligne with bundle for landscade
    public static final String BUNDLE_TEXT = "current text";


    // save text when smartphone landscape with bundle
    protected void onSaveInstanceState(Bundle outState) {
        String tmp_text = editTxtMultiligne.getText().toString();
        outState.putString( BUNDLE_TEXT, tmp_text );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_doc);

        btnSave = (MaterialButton) findViewById(R.id.btnSave);
        btnReturn = (MaterialButton) findViewById(R.id.btnReturn);
        btnClear = (MaterialButton) findViewById(R.id.btnClear);
        editTxtMultiligne = (EditText) findViewById(R.id.editTxtMultiligne);
//        checkBoxBold = (CheckBox) findViewById(R.id.checkBoxBold);
//        chekBoxItalic = (CheckBox) findViewById(R.id.checkBoxItalic);

        db = new DataBase( this );

        btnReturn.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnReturn.setTag(0);
        btnSave.setTag(1);
        btnClear.setTag(2);

        // if file selected in home page well loading in editTextMultilignes
        if( MainActivity.activateSelectedFile ) editTxtMultiligne.setText( MainActivity.TEXT_FILE );

        // add text when smartphone landscape with bundle
        if( savedInstanceState != null ) {
            editTxtMultiligne.setText( savedInstanceState.getString( BUNDLE_TEXT ) );
        }else{
            Log.i("info", "text clear bundle" );
        }


//        editTxtMultiligne.addTextChangedListener( txtwatcher );

    }


    // menu buttons
    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        int btnInt = ( int ) v.getTag();

        // button return clicked
        if ( btnInt == 0 ) {

            // go to home page ( to MainActivity )
            Intent accueilIntent = new Intent(ControlDocActivity.this, MainActivity.class );
            startActivity( accueilIntent );

            // button save clicked
        } else if ( btnInt == 1 ) {
            saveDocument();

            // button clear clicked
        } else if ( btnInt == 2 ) {
            clearText();
        }
    }

    // method to saved into Model class
    private void saveDocument() {
        texte = editTxtMultiligne.getText().toString();

        if ( !texte.isEmpty() ) {

            // alerteDialogue to save
            AlertDialog.Builder dialSave = new AlertDialog.Builder(this );
            dialSave.setTitle("file name :");

            // EditText in alerteDialogue save
            EditText editTitle = new EditText(this );
            editTitle.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
            editTitle.requestFocus();
            LinearLayout container = new LinearLayout( this );
            container.setOrientation( LinearLayout.HORIZONTAL );
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            lp.setMargins( 40, 0, 40, 0 );
            editTitle.setLayoutParams( lp );
            // if file selected well editText set the file name
            if( MainActivity.activateSelectedFile ) editTitle.setText( MainActivity.NAME_FILE );
            container.addView( editTitle, lp );
            dialSave.setView( container );

            // buttons of alertDialog to save
            dialSave.setPositiveButton("save", null )
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                        // button cancel in alertDialogue save
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast toastCancel = Toast.makeText( getApplicationContext(), "cancel", Toast.LENGTH_SHORT );
                            toastCancel.setGravity( Gravity.CENTER, 0, 0 );
                            toastCancel.show();
                        }
                    });
            AlertDialog dial = dialSave.create();
            dial.show();
            dial.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener(new View.OnClickListener() {

                // button save in alertDialog, if title and texte is true, well saved ! else, not saved
                @Override
                public void onClick(View v) {

                    // if selected file in home page well delete old data in database and save a new file
                    if ( !editTitle.getText().toString().isEmpty() ) {
                        if( MainActivity.activateSelectedFile ) {
                            db.deleteOldFile( MainActivity.NAME_FILE );
                        }

                        String title = editTitle.getText().toString();
                        modelDoc = new Model( title, texte );

                        // insert into database
                        db.insertIntoDatabase( modelDoc );

                        Toast toastSave = Toast.makeText( getApplicationContext(), "save", Toast.LENGTH_SHORT );
                        toastSave.setGravity(Gravity.CENTER, 0, 0);
                        toastSave.show();
                        dial.dismiss();

                        // go to home page ( MainActivity )
                        Intent homeIntent = new Intent(ControlDocActivity.this, MainActivity.class );
                        startActivity( homeIntent );

                    } else {
                        // if no title for file
                        Toast toastNotSave = Toast.makeText( getApplicationContext(), "enter a file name", Toast.LENGTH_SHORT );
                        toastNotSave.setGravity( Gravity.CENTER, 0, 0 );
                        toastNotSave.show();
                    }
                }
            });

        } else {
            // if editTextMultilines is empty
            Toast toastEmptyFile = Toast.makeText( getApplicationContext(), "text empty", Toast.LENGTH_SHORT );
            toastEmptyFile.setGravity(Gravity.CENTER, 0, 0);
            toastEmptyFile.show();
        }
    }
/*
    // method textChange on editTextMultilignes (checkbox bold and italic)
    public TextWatcher txtwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int _flag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
            int _start = editTxtMultiligne.getSelectionStart() - 1;
            int _end = editTxtMultiligne.getSelectionEnd();
            if( ( checkBoxBold.isChecked() ) && ( !chekBoxItalic.isChecked() ) ) {
                StyleSpan spanBold = new StyleSpan( Typeface.BOLD );
                editTxtMultiligne.getText().setSpan( spanBold, _start, _end, _flag);
            }else if ( ( chekBoxItalic.isChecked() ) && ( !checkBoxBold.isChecked() ) ) {
                StyleSpan spanItalic = new StyleSpan( Typeface.ITALIC );
                editTxtMultiligne.getText().setSpan( spanItalic, _start, _end, _flag);
            }else if( ( checkBoxBold.isChecked() ) && ( chekBoxItalic.isChecked() ) ) {
                StyleSpan spanBoldItalic = new StyleSpan( Typeface.BOLD_ITALIC );
                editTxtMultiligne.getText().setSpan( spanBoldItalic, _start, _end, _flag);
            }else{
                StyleSpan spanNormal = new StyleSpan( Typeface.NORMAL );
                editTxtMultiligne.getText().setSpan( spanNormal, _start, _end, _flag);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };
*/
    // method clear text in editTextMultilignes and reset textWatcher
    public void clearText() {
//        editTxtMultiligne.removeTextChangedListener( txtwatcher );
        editTxtMultiligne.setText( "" );
//        editTxtMultiligne.addTextChangedListener( txtwatcher );
    }



}