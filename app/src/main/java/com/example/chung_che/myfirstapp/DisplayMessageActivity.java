package com.example.chung_che.myfirstapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    TextView textView;
    int actionbarFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();

        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

        //TextView textView = new TextView(this);
        textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        setContentView(textView);

        // http://stackoverflow.com/questions/32072510/changing-action-bar-colour-programmatically-may-cause-nullpointerexception
        // if use themes with NoActionBar --> will get the NullPointerException
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    // the "settings" of app, so settings will not appear!!!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_message, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if ( id == R.id.action_clean ) {
            cleanTextView();
            return true;
        } else if ( id == R.id.action_reverse ) {
            reverseTextView();
            return true;
        } else if ( id == R.id.action_changeActionBar ) {
            changeActionBar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cleanTextView() {
        // empty string now, try to reverse it
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("");

        setContentView(textView);
    }

    public void reverseTextView() {
        // empty string now, try to reverse it

        CharSequence charSequence = textView.getText();
        int charSequenceLength = charSequence.length();

        String textViewString = charSequence.toString();
        char textViewArrayR[] = textViewString.toCharArray();


        for ( int i=0; i<charSequenceLength/2; i++ ) {
            char charHead = charSequence.charAt(i);
            char charTail = charSequence.charAt(charSequenceLength-i-1);
            textViewArrayR[i] = charTail;
            textViewArrayR[charSequenceLength-i-1] = charHead;
        }
        // update it
        textView.setText(textViewArrayR, 0, charSequenceLength);

        // this is not the text!!!!!
        //String textViewString2 = textView.toString();

    }

    public void changeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        Resources res = getResources();

        if ( actionbarFlag == 0 ) {
            // require api 21 (but with 8, not ok)
            //Drawable gradientBox = res.getDrawable(R.drawable.gradient_box02, null);

            // https://teamtreehouse.com/community/getdrawbleint-is-deprecated
            Drawable gradientBox = ContextCompat.getDrawable(this, R.drawable.gradient_box02);
            actionBar.setBackgroundDrawable(gradientBox);

            // also change title color
            // in styles <item name="android:textColor">#39fad6</item>
            getSupportActionBar().setTitle(Html.fromHtml(
                    "<font color='#ff0000'>I want the same string</font>"
            ));

            actionbarFlag = 1;
        } else {
            //Drawable gradientBox = res.getDrawable(R.drawable.gradient_box01, null);
            Drawable gradientBox = ContextCompat.getDrawable(this, R.drawable.gradient_box01);
            actionBar.setBackgroundDrawable(gradientBox);

            // <string name="title_activity_display_message">My Message</string>
            getSupportActionBar().setTitle(Html.fromHtml(
                    "<font color='#39fad6'>My Message</font>"
            ));

            actionbarFlag = 0;
        }
    }

}
