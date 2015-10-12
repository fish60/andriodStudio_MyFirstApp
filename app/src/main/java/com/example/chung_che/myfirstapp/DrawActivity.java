package com.example.chung_che.myfirstapp;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * drawing, hide status bar
 * hide action bar if we are drawing
 */

public class DrawActivity extends AppCompatActivity implements View.OnClickListener {

    // to be loaded later from resource
    // we can not use getResources() beforehand
    // in this way, do not use final (need set value later)
    private static int MAX_LINE_NOTE_TEXT = -1;

    RelativeLayout mDrawLayout = null;
    private EditText mNoteText = null;
    private QuadDrawView mQuadDrawView = null;

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
        mNoteText.setOnTouchListener(editTextListener);

        // set max line (using online code~)
        setMaxLineNoteText(mNoteText, MAX_LINE_NOTE_TEXT);

        mDrawLayout = (RelativeLayout) findViewById(R.id.drawLayout);
        mDrawLayout.setOnClickListener(this);


        // set view for reappear
        mQuadDrawView = (QuadDrawView) findViewById(R.id.quadDrawView);
        mQuadDrawView.setView(mNoteText);
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

            // hide
            hideInput(findViewById(R.id.drawLayout));

            // get view
            //QuadDrawView quadDrawView = (QuadDrawView) findViewById(R.id.quadDrawView);

            // clean
            mQuadDrawView.clean();

            mQuadDrawView.setEnabled(false);



            // enable
            mQuadDrawView.setEnabled(true);

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

    @Override
    public void onClick(View view) {
        if ( view.getId() == R.id.drawLayout ) {
            hideInput(view);
        }
    }

    private void hideInput(View view) {
        mNoteText.clearFocus();
        //mNoteText.setCursorVisible(false);
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void hideEditText() {
        mNoteText.setVisibility(View.INVISIBLE);
    }

    private void showEditText() {
        mNoteText.setVisibility(View.VISIBLE);
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

    // http://blog.xuite.net/viplab/blog/250768633-%5BAndroid%5D%E9%9A%A8%E6%89%8B%E6%8C%87%E7%A7%BB%E5%8B%95%E7%9A%84ImageView
    // for image view, this is fine
    //
    private View.OnTouchListener editTextListener = new View.OnTouchListener() {
        private float x, y;    // 原本圖片存在的X,Y軸位置
        private int mx, my;    // 圖片被拖曳的X ,Y軸距離長度

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {          //判斷觸控的動作

                case MotionEvent.ACTION_DOWN:// 按下圖片時
                    x = event.getX();                  //觸控的X軸位置
                    y = event.getY();                  //觸控的Y軸位置

                case MotionEvent.ACTION_MOVE:// 移動圖片時

                    //getX()：是獲取當前控件(View)的座標
                    //getRawX()：是獲取相對顯示螢幕左上角的座標

                    int dx = (int) event.getRawX() - (int) x;
                    int dy = (int) event.getRawY() - (int) y;//横坐标和纵坐标移动的距离
                    //int left = v.getLeft() + dx;
                    //int top = v.getTop() + dy;
                    int right = dx + v.getWidth();   //v.getRight() + dx;
                    int bottom = dy + v.getHeight(); //v.getBottom() + dy;
                    /*
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }
                    if ( right > mDrawLayout.getMeasuredWidth() ) {
                        right = mDrawLayout.getMeasuredWidth();
                        left = right - v.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }
                    if ( bottom > mDrawLayout.getMeasuredHeight() ) {
                        bottom = mDrawLayout.getMeasuredHeight();
                        top = bottom - v.getHeight();
                    }
                    */
                    if (dx < 0) {
                        dx = 0;
                        right = dx + v.getWidth();
                    }
                    if ( right > mDrawLayout.getMeasuredWidth() ) {
                        right = mDrawLayout.getMeasuredWidth();
                        dx = right - v.getWidth();
                    }
                    // 改成 action bar + shift y
                    if (dy - 100 < 0) {
                        dy = 100;
                        bottom = dy + v.getHeight();
                    }
                    // 改成 3 行 (const max line) 總高 + shift y
                    if ( bottom + 100 > mDrawLayout.getMeasuredHeight() ) {
                        bottom = mDrawLayout.getMeasuredHeight() - 100;
                        dy = bottom - v.getHeight();
                    }

                    // l   Left position, relative to parent
                    // t    Top position, relative to parent
                    // r  Right position, relative to parent
                    // b Bottom position, relative to parent
                    //v.layout(left, top, right, bottom);
                    v.layout(dx, dy, right, bottom);
                    //x = (int) event.getRawX();
                    //y = (int) event.getRawY();


                    // right code
                    //mx = (int) (event.getRawX() - x);
                    //my = (int) (event.getRawY() - y);
                    //v.layout(mx, my, mx + v.getWidth(), my + v.getHeight());
                    break;
            }

            return true;
        }
    };
}
