package com.example.chung_che.myfirstapp;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.ImageView;
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

    // TODO
    // get 4 padding value instead of 2
    // if someone uses non-symmetric padding ...
    private static int PADDING_HORIZONTAL = -1;
    private static int PADDING_VERTICAL = -1;

    // dimens.xml dp, not getDimensionPixelSize()
    private static int PADDING_VERTICAL_DP = -1;

    private static int HEIGHT_STATUS_BAR = 0;

    // TODO
    // maybe use 2 settings for 直橫
    private final String NOTE_POSITION = "notePosition";

    private RelativeLayout mDrawLayout = null;
    private QuadDrawView mQuadDrawView = null;
    private EditText mNoteText = null;

    private ImageView mMoveImageView = null;

    private boolean mIsMovingMode = false;
    private int[] mStoredNotePosition = {-1, -1};

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
            Resources res = getResources();

            MAX_LINE_NOTE_TEXT = res.getInteger(R.integer.const_max_input_line);

            //  check if we need / res.getDisplayMetrics().density
            PADDING_HORIZONTAL = (int) ( res.getDimension(R.dimen.activity_horizontal_margin_full_screen) );
            PADDING_VERTICAL = (int) ( res.getDimension(R.dimen.activity_vertical_margin_full_screen) );

            // get int value for moveListener
            // calling v.layout(dx, dy, right, bottom);
            // ex: from 20dp --> 40
            //Toast.makeText(this, "PADDING_HORIZONTAL: " + ((Integer)PADDING_HORIZONTAL).toString(), Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "PADDING_VERTICAL: " + ((Integer)PADDING_VERTICAL).toString(), Toast.LENGTH_LONG).show();

            // res.getDisplayMetrics().density = 2 (Zenfone ZE600KL)

            // 12 dp (from dimens.xml) --> 24 / 2
            PADDING_VERTICAL_DP = (int) (res.getDimension(R.dimen.activity_vertical_margin_full_screen)
                    / res.getDisplayMetrics().density);
            Toast.makeText(this, "PADDING_VERTICAL_DP: " + ((Integer)PADDING_VERTICAL_DP).toString(), Toast.LENGTH_LONG).show();

            int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                // 50
                HEIGHT_STATUS_BAR = res.getDimensionPixelSize(resourceId);
                // 50
                //HEIGHT_STATUS_BAR = (int) ( res.getDimension(resourceId) ); // float
                Toast.makeText(this, "HEIGHT_STATUS_BAR: " + ((Integer)HEIGHT_STATUS_BAR).toString(), Toast.LENGTH_LONG).show();
            }
        }


        setContentView(R.layout.activity_draw);

        // assignment after setContentView()
        //
        mDrawLayout = (RelativeLayout) findViewById(R.id.drawLayout);
        mNoteText = (EditText) findViewById(R.id.noteText);
        mQuadDrawView = (QuadDrawView) findViewById(R.id.quadDrawView);
        // image view for testing
        // move to new position / input using edit text
        mMoveImageView = (ImageView) findViewById(R.id.testMoveImageView);


        // for state transition, check code for detail
        mDrawLayout.setOnClickListener(this);
        // set max line (using online code)
        setMaxLineNoteText(mNoteText, MAX_LINE_NOTE_TEXT);
        // set view for reappear
        mQuadDrawView.setView(mNoteText);
        // touch and move, within a range
        mMoveImageView.setOnTouchListener(moveListener);


        if ( savedInstanceState != null ) {
            int[] positionArray = savedInstanceState.getIntArray(NOTE_POSITION);
            // 記得可以寫成一行, 前面的會先判斷
            if ( positionArray != null ) {
                //Toast.makeText(this, "......", Toast.LENGTH_LONG).show();

                if ( positionArray.length == 2 ) {

                    // (0, 0) ......
                    int[] editTextPosition = {-1, -1};
                    mNoteText.getLocationOnScreen(editTextPosition);
                    Toast.makeText(this, "editTextPosition NOW: ("
                                    + ((Integer)editTextPosition[0]).toString() + ", "
                                    + ((Integer)editTextPosition[1]).toString() + ")",
                            Toast.LENGTH_LONG).show();

                    // right value
                    Toast.makeText(this, "positionArray: ("
                                    + ((Integer)positionArray[0]).toString() + ", "
                                    + ((Integer)positionArray[1]).toString() + ")",
                            Toast.LENGTH_LONG).show();

                    // useless now
                    mStoredNotePosition = positionArray;
                    mNoteText.layout(positionArray[0],
                            positionArray[1],
                            positionArray[0] + mNoteText.getWidth(),
                            positionArray[1] + mNoteText.getHeight());

                    Toast.makeText(this, "mStoredNotePosition: ("
                                    + ((Integer)mStoredNotePosition[0]).toString() + ", "
                                    + ((Integer)mStoredNotePosition[1]).toString() + ")",
                            Toast.LENGTH_LONG).show();


                    if ( positionArray[0] != -1 ) {

                        RelativeLayout.LayoutParams mNoteTextLayoutParams =
                                (RelativeLayout.LayoutParams) mNoteText.getLayoutParams();

                        mNoteTextLayoutParams.setMargins(
                                positionArray[0] - PADDING_HORIZONTAL,
                                positionArray[1] - HEIGHT_STATUS_BAR - PADDING_VERTICAL_DP,
                                0, 0);
                        mNoteText.setLayoutParams(mNoteTextLayoutParams);

                        //RelativeLayout.LayoutParams layoutParams =
                        //        new RelativeLayout.LayoutParams(mNoteText.getWidth(), mNoteText.getHeight());
                        //layoutParams.setMargins(positionArray[0]-32, positionArray[1]-32, 0, 0);
                        //mNoteText.setLayoutParams(layoutParams);
                        //mNoteText.setMaxLines(3);
                    }


                }
            }

            // useless if before
            // mNoteText = (EditText) findViewById(R.id.noteText);
            // (182, 220) --> 32, 70
            /*
            RelativeLayout.LayoutParams mNoteTextLayoutParams =
                    (RelativeLayout.LayoutParams) mNoteText.getLayoutParams();
            mNoteTextLayoutParams.setMargins(150, 150, 0, 0);
            mNoteText.setLayoutParams(mNoteTextLayoutParams);
            */
        } else {

            // 16dp, 16dp
            // --> 232, 270, 多了 32, 70
            // 32, 70 應該跟某些東西有關連, 要找出來不然應該之後會再有 bug
            // 32 = PADDING_HORIZONTAL = 16dp * 2 [res.getDisplayMetrics().density]
            // 70 = PADDING_VERTICAL + ? = 16dp * 2 + 38
            // 20dp, 12dp
            // --> 240, 262 --> 40, 62 = 20 * 2, 12 * 2 + 38
            // = 20 * 2, 12 + 50
            RelativeLayout.LayoutParams mNoteTextLayoutParams =
                    (RelativeLayout.LayoutParams) mNoteText.getLayoutParams();
            mNoteTextLayoutParams.setMargins(200 - PADDING_HORIZONTAL,
                    200 - HEIGHT_STATUS_BAR - PADDING_VERTICAL_DP,
                    0, 0);
            mNoteText.setLayoutParams(mNoteTextLayoutParams);

        }







        // need after setContentView(), get EditText variable for noteText
        //mNoteText = (EditText) findViewById(R.id.noteText);
        //mNoteText.setSaveEnabled(false);
        //mNoteText.setOnTouchListener(null);

        // useless ......
        /*
        mNoteText.layout(140,
                140,
                140 + mNoteText.getWidth(),
                140 + mNoteText.getHeight());
                */





    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.

        // (0, 0) after onCreate()
        // right value back from onStop()
        /*
        int[] editTextPosition = {-1, -1};
        mNoteText.getLocationOnScreen(editTextPosition);
        Toast.makeText(this, "editTextPosition onStart(): ("
                        + ((Integer)editTextPosition[0]).toString() + ", "
                        + ((Integer)editTextPosition[1]).toString() + ")",
                Toast.LENGTH_LONG).show();
                */

    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").

        // (0, 0) after onCreate() --> onStart()
        // right value back from onPause()
        /*
        int[] editTextPosition = {-1, -1};
        mNoteText.getLocationOnScreen(editTextPosition);
        Toast.makeText(this, "editTextPosition onResume(): ("
                        + ((Integer)editTextPosition[0]).toString() + ", "
                        + ((Integer)editTextPosition[1]).toString() + ")",
                Toast.LENGTH_LONG).show();
        */

        // keyboard --> settings --> back --> can perform .layout()
        /*
        mNoteText.layout(140,
                140,
                140 + mNoteText.getWidth(),
                140 + mNoteText.getHeight());
                */
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The activity has become visible (it is now "resumed").

        // (0, 0)
        // 20dp, 12dp
        // 200, 200 --> 240, 262
        int[] editTextPosition = {-1, -1};
        mNoteText.getLocationOnScreen(editTextPosition);
        Toast.makeText(this, "editTextPosition onPause(): ("
                        + ((Integer)editTextPosition[0]).toString() + ", "
                        + ((Integer)editTextPosition[1]).toString() + ")",
                Toast.LENGTH_LONG).show();

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
        } else if (id == R.id.action_moveText) {

            // hide keyboard input
            hideInput(mDrawLayout);

            mIsMovingMode = true;
            mNoteText.setOnTouchListener(moveListener);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if ( view.getId() == R.id.drawLayout ) {
            hideInput(view);
        }
        if ( mIsMovingMode ) {
            mNoteText.setOnTouchListener(null);
        }

        int[] editTextPosition = {-1, -1};
        mNoteText.getLocationOnScreen(editTextPosition);
        Toast.makeText(this, "editTextPosition: ("
                        + ((Integer)editTextPosition[0]).toString() + ", "
                        + ((Integer)editTextPosition[1]).toString() + ")",
                Toast.LENGTH_LONG).show();

        if ( mStoredNotePosition[0] != -1 ) {
            mNoteText.layout(mStoredNotePosition[0],
                    mStoredNotePosition[1],
                    mStoredNotePosition[0] + mNoteText.getWidth(),
                    mStoredNotePosition[1] + mNoteText.getHeight());
        }
        if ( editTextPosition[0] != -1 ) {
            mNoteText.layout(editTextPosition[0],
                    editTextPosition[1],
                    editTextPosition[0] + mNoteText.getWidth(),
                    editTextPosition[1] + mNoteText.getHeight());
        }


        /*
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(mMoveImageView.getWidth(), mMoveImageView.getHeight());
        layoutParams.setMargins(100, 10, 0, 0);
        mMoveImageView.setLayoutParams(layoutParams);
        */

        if ( editTextPosition[0] != -1 ) {
            //layoutParams =
            //        //new RelativeLayout.LayoutParams(mNoteText.getWidth(), mNoteText.getHeight());
            //        new RelativeLayout.LayoutParams(mNoteText.getWidth(), mNoteText.getHeight());
            //layoutParams.setMargins(editTextPosition[0]-32, editTextPosition[1]-32, 0, 0);
            //mNoteText.setLayoutParams(layoutParams);
            //mNoteText.setMaxLines(3);
            RelativeLayout.LayoutParams mNoteTextLayoutParams =
                    (RelativeLayout.LayoutParams) mNoteText.getLayoutParams();

            // 16dp, 16dp --> 32 70 = 32, 32 + 38
            // 20dp, 12dp --> 40 62 = 40, 24 + 38
            mNoteTextLayoutParams.setMargins(editTextPosition[0] - PADDING_HORIZONTAL,
                    editTextPosition[1] - HEIGHT_STATUS_BAR - PADDING_VERTICAL_DP,
                    0, 0);
            mNoteText.setLayoutParams(mNoteTextLayoutParams);
        }
        //mMoveImageView.requestLayout();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        int[] editTextPosition = {-1, -1};
        mNoteText.getLocationOnScreen(editTextPosition);
        savedInstanceState.putIntArray(NOTE_POSITION, editTextPosition);

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
            //int[] editTextPosition = {-1, -1};

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                /*
                editText.getLocationOnScreen(editTextPosition);
                if ( editTextPosition[0] != -1 ) {
                    editText.layout(editTextPosition[0],
                            editTextPosition[1],
                            editTextPosition[0] + editText.getWidth(),
                            editTextPosition[1] + editText.getHeight());
                }
                */

                lastCursorPosition = editText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                /*
                editText.getLocationOnScreen(editTextPosition);
                if ( editTextPosition[0] != -1 ) {
                    editText.layout(editTextPosition[0],
                            editTextPosition[1],
                            editTextPosition[0] + editText.getWidth(),
                            editTextPosition[1] + editText.getHeight());
                }
                */

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                editText.getLocationOnScreen(editTextPosition);
                if ( editTextPosition[0] != -1 ) {
                    editText.layout(editTextPosition[0],
                            editTextPosition[1],
                            editTextPosition[0] + editText.getWidth(),
                            editTextPosition[1] + editText.getHeight());
                }
                */

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
    // for view with edit text, the position will be reset
    //
    // can not add method for outsider, need extend ?
    //
    private View.OnTouchListener moveListener = new View.OnTouchListener() {
        private float x, y;    // 原本圖片存在的X,Y軸位置
        private int mx, my;    // 圖片被拖曳的X ,Y軸距離長度

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {          //判斷觸控的動作

                case MotionEvent.ACTION_DOWN:// 按下圖片時
                    x = event.getX();                  //觸控的X軸位置
                    y = event.getY();                  //觸控的Y軸位置

                    //hasDown = true;

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
                    // handling the boundary condition
                    // add more value if needed
                    if (dx - PADDING_HORIZONTAL < 0) {
                        dx = PADDING_HORIZONTAL;
                        right = dx + v.getWidth();
                    }
                    if ( right + PADDING_HORIZONTAL > mDrawLayout.getMeasuredWidth() ) {
                        right = mDrawLayout.getMeasuredWidth() - PADDING_HORIZONTAL;
                        dx = right - v.getWidth();
                    }
                    // 改成 action bar + shift y
                    if (dy - 100 - PADDING_VERTICAL_DP < 0) {
                        dy = 100 + PADDING_VERTICAL_DP;
                        bottom = dy + v.getHeight();
                    }
                    // 改成 3 行 (const max line) 總高 + shift y
                    if ( bottom + 100 + PADDING_VERTICAL_DP > mDrawLayout.getMeasuredHeight() ) {
                        bottom = mDrawLayout.getMeasuredHeight() - 100 + PADDING_VERTICAL_DP;
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
