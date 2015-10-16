package com.example.chung_che.myfirstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JimCC_Yu on 2015/10/8.
 * https://github.com/johncarl81/androiddraw/blob/master/src/main/java/org/example/androiddraw/QuadDrawView.java
 *
 * when drawing, hide action bar and other view
 * other view: edit text
 *
 */
public class QuadDrawView extends View implements View.OnTouchListener {

    // TODO
    // record Path for init an existing draw

    //private boolean mEnable = false;

    // we set view and handle it
    // not sure if this is fine or not (ex: memory)
    private View mView = null;

    private static final float STROKE_WIDTH = 5f;

    //private Canvas mCanvas = null;
    private Path mPath;

    List<Point> points = new ArrayList<Point>();
    Paint paint = new Paint();

    public QuadDrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        // false it
        this.setEnabled(false);

        mPath = new Path();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //mPath.addPath();

        // this speed it up, or we need to use new Path() for speeding up
        mPath.reset();

        //Path path = new Path();
        boolean first = true;
        for(int i = 0; i < points.size(); i += 2){
            Point point = points.get(i);
            if(first){
                first = false;
                //path.moveTo(point.x, point.y);
                mPath.moveTo(point.x, point.y);
            }

            else if(i < points.size() - 1){
                Point next = points.get(i + 1);
                //path.quadTo(point.x, point.y, next.x, next.y);
                mPath.quadTo(point.x, point.y, next.x, next.y);
            }
            else{
                //path.lineTo(point.x, point.y);
                mPath.lineTo(point.x, point.y);
            }
        }

        //canvas.drawPath(path, paint);
        canvas.drawPath(mPath, paint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if ( this.isEnabled() ) {
            if (event.getAction() != MotionEvent.ACTION_UP) {
                for (int i = 0; i < event.getHistorySize(); i++) {
                    Point point = new Point();
                    point.x = event.getHistoricalX(i);
                    point.y = event.getHistoricalY(i);
                    points.add(point);
                }
                invalidate();
                return true;
            }

            // TODO
            // re-enable drawing many times

            // once up, show actionbar, end of drawing
            // thus we could clean old drawing when hiding actionbar

            // use general one
            //ActionBar actionBar = ((DrawActivity)getContext()).getSupportActionBar();
            ActionBar actionBar = ((AppCompatActivity)getContext()).getSupportActionBar();
            if ( actionBar != null ) {
                actionBar.show();
            }

            // show view (edit text)
            if ( mView != null ) {
                mView.setVisibility(View.VISIBLE);
            }


            //paint.setColor(Color.RED);
            //invalidate();
            this.setEnabled(false);
        }



        return super.onTouchEvent(event);
    }

    public void clear() {
        points.clear();
    }

    /*
    public void setEnable(boolean enable) {
        mEnable = enable;
    }
    */

    public void clean() {

        // since we hide the actionbar
        // this would let the onDraw() be called soon
        // otherwise need ex: one touch event ...
        // invalidate() will not redraw!
        // only ensure to call onDraw() LATER


        // use general one
        //ActionBar actionBar = ((DrawActivity)getContext()).getSupportActionBar();
        ActionBar actionBar = ((AppCompatActivity)getContext()).getSupportActionBar();
        if ( actionBar != null ) {
            actionBar.hide();
        }

        // hide view (edit text)
        if ( mView != null ) {
            mView.setVisibility(View.INVISIBLE);
        }

        clear();
        mPath.reset();

        invalidate();


    }

    public void setView(View view) {
        mView = view;
    }

    class Point {
        float x, y;
        float dx, dy;

        @Override
        public String toString() {
            return x + ", " + y;
        }
    }

}

