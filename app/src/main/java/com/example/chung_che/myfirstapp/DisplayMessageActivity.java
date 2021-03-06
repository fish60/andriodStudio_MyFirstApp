package com.example.chung_che.myfirstapp;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
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

/*
 * 2015/09/21
 * call res
 *     ContextCompat.getColor
 * add color.xml
 *     color def
 * modify strings.xml
 *     font color def
 * color for setTitle(Html.fromHtml())
 *     String.format
 * add private method _changeActionBar() for reducing duplicate codes
 *
 *
 *
 * */

/*
 * 2015/10/01
 * SharedPreferences
 * save some preferences (ex: ("actionbarFlag", mActionbarFlag) thus even use Back, we could restore the status of actionbar)
 * add codes to onCreate() and onStop()
 *
 *
 * */

public class DisplayMessageActivity extends AppCompatActivity {
    private final String CURRENT_ACTIONBAR_FLAG = "current_actionBarFlag";
    public static final String PREFS_NAME = "MyPrefsFile";

    // private
    // textView --> mTextView
    private TextView mTextView;

    // private
    // actionbarFlag --> mActionbarFlag
    private int mActionbarFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();

        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

        //TextView textView = new TextView(this);
        mTextView = new TextView(this);
        mTextView.setTextSize(40);
        mTextView.setText(message);

       setContentView(mTextView);

        // http://stackoverflow.com/questions/32072510/changing-action-bar-colour-programmatically-may-cause-nullpointerexception
        // if use themes with NoActionBar --> will get the NullPointerException
        ActionBar currentActionBar = getSupportActionBar();

        if ( currentActionBar != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if ( savedInstanceState != null ) {
                // restore action bar mode
                mActionbarFlag = savedInstanceState.getInt(CURRENT_ACTIONBAR_FLAG);
                restoreActionBar();
            } else {
                // no savedInstanceState
                // Restore preferences
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                mActionbarFlag = settings.getInt("actionbarFlag", mActionbarFlag);
                restoreActionBar();
            }

        }

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
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("actionbarFlag", mActionbarFlag);

        // Commit the edits!
        editor.commit();
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

    // save state, ex: action bar mode using flag
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(CURRENT_ACTIONBAR_FLAG, mActionbarFlag);

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

        CharSequence charSequence = mTextView.getText();
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
        mTextView.setText(textViewArrayR, 0, charSequenceLength);

        // this is not the text!!!!!
        //String textViewString2 = textView.toString();

    }

    // restore it for ex: rotating the screen
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        // actionBar is null
        if ( actionBar == null ) {
            return ;
        }

        if ( mActionbarFlag == 0 ) {
            _changeActionBar(actionBar, R.drawable.gradient_box01, R.color.blue_green);
        } else {
            _changeActionBar(actionBar, R.drawable.gradient_box02, R.color.opaque_red);
        }
    }

    public void changeActionBar() {
        ActionBar actionBar = getSupportActionBar();

        // actionBar is null
        if ( actionBar == null ) {
            return ;
        }

        if ( mActionbarFlag == 0 ) {
            _changeActionBar(actionBar, R.drawable.gradient_box02, R.color.opaque_red);
            mActionbarFlag = 1;
        } else {
            _changeActionBar(actionBar, R.drawable.gradient_box01, R.color.blue_green);
            mActionbarFlag = 0;
        }

        /*
                 * old version with comments

        Resources res = getResources();

        if ( mActionbarFlag == 0 ) {
            // require api 21 (but with 8, not ok)
            //Drawable gradientBox = res.getDrawable(R.drawable.gradient_box02, null);

            // https://teamtreehouse.com/community/getdrawbleint-is-deprecated
            Drawable gradientBox = ContextCompat.getDrawable(this, R.drawable.gradient_box02);

            actionBar.setBackgroundDrawable(gradientBox);

            // also change title color
            // in styles <item name="android:textColor">#39fad6</item>

            // use res
            //getSupportActionBar().setTitle(Html.fromHtml(
            //    "<font color='#ff0000'>I want the same string</font>"
            //String command = "<font color='#ff0000'>";

            // get color as int, then remove alpha channel with hex code for Html.fromHtml()
            int targetColor = ContextCompat.getColor(this, R.color.opaque_red);
            String targetColorHtml = String.format("#%06X", (0xFFFFFF &
                    Color.argb(0, Color.red(targetColor), Color.green(targetColor), Color.blue(targetColor))));

            actionBar.setTitle(Html.fromHtml(
                    res.getString(R.string.font_color_head_01) +
                            targetColorHtml +
                            res.getString(R.string.font_color_head_02) +
                            res.getString(R.string.title_activity_display_message) +
                            res.getString(R.string.font_color_tail)
            ));

            // Integer.toHexString(ContextCompat.getColor(this, R.color.opaque_red))

            //Integer.toHexString(id)
            // level 23
            //int color = res.getColor(R.color.opaque_red, null);
            //Integer.toHexString(color);
            // --> ContextCompat.getColor(context, R.color.my_color)

            // for debug, show partial info
            // My Message
            //actionBar.setTitle(res.getString(R.string.title_activity_display_message));
            // ffff0000 --> but only need ff0000
            //actionBar.setTitle(Integer.toHexString(ContextCompat.getColor(this, R.color.opaque_red)));
            // use ffff0000 --> no change --> alpha not support with font
            // use 00ff0000 --> ok, but the color is ffff0000 --> WHY????? --> alpha not support with font
            // use ff0000 --> ok, need use 6 digits only
            // #%06X --> ex: #FF0000, capital is fine
            //actionBar.setTitle(targetColorHtml);


            mActionbarFlag = 1;
        } else {
            //Drawable gradientBox = res.getDrawable(R.drawable.gradient_box01, null);
            Drawable gradientBox = ContextCompat.getDrawable(this, R.drawable.gradient_box01);
            actionBar.setBackgroundDrawable(gradientBox);

            // <string name="title_activity_display_message">My Message</string>
            //getSupportActionBar().setTitle(Html.fromHtml(
            actionBar.setTitle(Html.fromHtml(
                    "<font color='#39fad6'>My Message</font>"
            ));

            mActionbarFlag = 0;
        }
        */
    }

    // change action bar
    // input: res drawable for setBackgroundDrawable()
    // input: res color for setTitle()
    private void _changeActionBar(ActionBar actionBar, int drawableBackground, int colorTitle) {

        //  action bar background with drawable
        Drawable gradientBox = ContextCompat.getDrawable(this, drawableBackground);
        actionBar.setBackgroundDrawable(gradientBox);

        // action bar title with color
        int targetColor = ContextCompat.getColor(this, colorTitle);
        String targetColorHtml = String.format("#%06X", (0xFFFFFF &
                Color.argb(0, Color.red(targetColor), Color.green(targetColor), Color.blue(targetColor))));

        Resources res = getResources();

        actionBar.setTitle(Html.fromHtml(
                res.getString(R.string.font_color_head_01) +
                        targetColorHtml +
                        res.getString(R.string.font_color_head_02) +
                        res.getString(R.string.title_activity_display_message) +
                        res.getString(R.string.font_color_tail)
        ));

    }


}
