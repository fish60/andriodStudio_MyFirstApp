package com.example.chung_che.myfirstapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * drawing, hide status bar
 * hide action bar if we are drawing
 */

public class DrawActivity extends AppCompatActivity {

    // to be loaded later from resource
    // we can not use getResources() beforehand
    // in this way, do not use final (need set value later)
    private static int MAX_LINE_NOTE_TEXT = -1;


    private EditText mNoteText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        // hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // use full area
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        // the window will stay fullscreen and "will not resize"
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);



        // init const here
        if ( MAX_LINE_NOTE_TEXT < 0 ) {
            MAX_LINE_NOTE_TEXT = getResources().getInteger(R.integer.const_max_input_line);
        }


        setContentView(R.layout.activity_draw);




        // need after setContentView(), get EditText variable for noteText
        mNoteText = (EditText) findViewById(R.id.noteText);

        // set max line (using online code~)
        setMaxLineNoteText(mNoteText, MAX_LINE_NOTE_TEXT);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_drawIt) {
            // hide action bar

            // get view
            QuadDrawView quadDrawView = (QuadDrawView) findViewById(R.id.quadDrawView);

            // clean
            quadDrawView.clean();

            quadDrawView.setEnabled(false);





            // enable
            quadDrawView.setEnabled(true);

            /*
            String text = mNoteText.getText().toString();


            int editTextRowCount = text.split("\\n").length;
            Toast.makeText(this,
                    "editTextRowCount: " + ((Integer)editTextRowCount).toString(),
                    Toast.LENGTH_LONG).show();

            editTextRowCount = mNoteText.getLineCount();
            Toast.makeText(this,
                    "editTextRowCount: " + ((Integer)editTextRowCount).toString(),
                    Toast.LENGTH_LONG).show();
            */


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // http://stackoverflow.com/questions/7092961/edittext-maxlines-not-working-user-can-still-input-more-lines-than-set
    // if maxLineNoteText <= 0, can not input any text (will be set "")
    private void setMaxLineNoteText(final EditText editText, final int maxLineNoteText) {

        if ( editText == null ) {
            return;
        }

        editText.addTextChangedListener(new TextWatcher() {
            private int lastCursorPosition = 0;
            private String currentText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastCursorPosition = editText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);

                if (editText.getLineCount() > maxLineNoteText) {
                    editText.setText(currentText);
                    editText.setSelection(lastCursorPosition);
                } else
                    currentText = editText.getText().toString();

                editText.addTextChangedListener(this);
            }
        });
    }
}
