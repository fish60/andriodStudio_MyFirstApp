package com.example.chung_che.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * 注意輸入的字何時會消失何時會保存
 * 按左上的 back 與左下預設 back 有差
 * 連中圖預覽都會不一致... 如果在 focus 下 左下 back 就是預覽有, 進入無
 * 想辦法處理? (正確的系統結果但是使用者會覺得怪)
 * 與 restore 有關???
 */

/*
 * Toast.makeText
 * fade out msg 但是會 pending 很多個的時候就要等
 * 且可以在外面看到
 * http://magiclen.org/android-toast/ 教學
 */

public class MyActivity extends AppCompatActivity {

    // 管理此 activity 的 Toast, 快速顯示
    private static Toast toast;

    private final static int IMAGE_GALLERY = 100;
    // "com.mycompany.myfirstapp.MESSAGE" --> change ???
    public final static String EXTRA_MESSAGE = "com.example.chung_che.myfirstapp.MESSAGE";

    // for Bitmap compress, but if use png, ignore this
    private final static int COMPRESS_QUALITY = 100;

    private Bitmap mSelectedImage = null;
    private ImageView mViewScreenshot = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);



        mViewScreenshot = (ImageView) findViewById(R.id.viewScreenshot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */
        switch (id) {
            case R.id.action_settings:

                return true;
            case R.id.action_send:
                openSend();
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }


        //return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ) {
            if (requestCode == IMAGE_GALLERY) {
                // response from image gallery

                // move this to --> resultCode == RESULT_CANCELED
                /*
                // only a null init, may not be needed, but still an init step
                Uri picDir = null;
                try {
                    picDir = data.getData();
                } catch (NullPointerException e) {
                    // get exception if user uses "Back"
                    // (not mentioned in video https://www.youtube.com/watch?v=zZDFKy_mVPg)

                    e.printStackTrace();

                    // Toast.makeText
                    // will show msg --> fade out
                    Toast.makeText(this, getString(R.string.msg_selectNothing), Toast.LENGTH_LONG).show();
                    return;
                }
                                */

                Uri picDir = data.getData();

                // temp msg
                if ( mSelectedImage == null ) {
                    //Toast.makeText(this, "First Get", Toast.LENGTH_LONG).show();
                    makeTextAndShow(this, "First Get", Toast.LENGTH_SHORT);
                } else {
                    //Toast.makeText(this, "Get New One", Toast.LENGTH_LONG).show();
                    makeTextAndShow(this, "Get New One", Toast.LENGTH_SHORT);
                }

                try {
                    InputStream openedStream = getContentResolver().openInputStream(picDir);

                    // stream of data --> Bitmap
                    mSelectedImage = BitmapFactory.decodeStream(openedStream);

                    // set image to a view
                    mViewScreenshot.setImageBitmap(mSelectedImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                    //Toast.makeText(this, getString(R.string.msg_unableToOpenImage), Toast.LENGTH_LONG).show();
                    makeTextAndShow(this, getString(R.string.msg_unableToOpenImage), Toast.LENGTH_LONG);
                    return;
                }
                //Toast.makeText(this, getString(R.string.msg_unableToOpenImage), Toast.LENGTH_LONG).show();


            }
        } else if ( resultCode == RESULT_CANCELED ) {
            if (requestCode == IMAGE_GALLERY) {
                //Toast.makeText(this, getString(R.string.msg_selectNothing), Toast.LENGTH_LONG).show();
                makeTextAndShow(this, getString(R.string.msg_selectNothing), Toast.LENGTH_SHORT);
            }
        }
    }


    // show toast, instantly replace existing one
    private static void makeTextAndShow(final Context context, final String text, final int duration) {
        if (toast == null) {
            //如果還沒有用過makeText方法，才使用
            toast = android.widget.Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }


    // try to figure out the input View view

    // public --> for button (or we need more code to fo it when using private)
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // action bar menu
    private void openSend() {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // https://www.youtube.com/watch?v=zZDFKy_mVPg
    // button
    public void openImage(View view) {
        // open image using intent
        Intent intent = new Intent(Intent.ACTION_PICK);

        // path
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();

        // parse
        Uri picDir = Uri.parse(path);

        intent.setDataAndType(picDir, "image/*");

        startActivityForResult(intent, IMAGE_GALLERY);
    }

    // button
    public void takeScreenShotAndSave(View view) {
        // take screenshot, also save it

        int id = view.getId();

        switch (id) {
            case R.id.buttonScreenShot1:
                makeTextAndShow(this, "buttonScreenShot 1", Toast.LENGTH_SHORT);
                break;
            case R.id.buttonScreenShot2:
                makeTextAndShow(this, "buttonScreenShot 2", Toast.LENGTH_SHORT);
                break;


            default:


        }



        // Only get the button, bad view input
        //Bitmap screenShot = takeScreenShot(view);

        // This works, use this (AppCompatActivity is an Activity) --> getWindow() -->
        Bitmap screenShot = takeScreenShot(this.getWindow().getDecorView());

        //Bitmap screenShot2 = takeScreenShot2(view);
        Bitmap screenShot2 = takeScreenShot2(this.getWindow().getDecorView());

        // save ?
        if ( screenShot != null ) {
            // show it first
            //mViewScreenshot.setImageBitmap(screenShot);

            mViewScreenshot.setImageBitmap(screenShot2);

            //saveScreenShot(screenShot);
        }


        // show ?

    }
    private Bitmap takeScreenShot2(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        //得到狀態列高度
        Rect frame = new Rect();

        AppCompatActivity activity = this;
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        // ex: 50
        //makeTextAndShow(this, ((Integer) statusBarHeight).toString(), Toast.LENGTH_SHORT);

        //得到螢幕長和高
        // ... deprecated
        //int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        //int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        // use getWindowManager(), no need activity?
        // --> version
        //Point screenSize = new Point();
        //activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        //int width = screenSize.x;
        //int height = screenSize.y;

        // http://stackoverflow.com/questions/23277500/android-call-requires-api-level-13-current-min-is-10-android-view-displayget
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //去掉標題列
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);

        // 應該是 top 狀態列才對, 不過是黑的......
        //Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);

        // care IllegalArgumentException

        view.destroyDrawingCache();
        return b;
    }

    private Bitmap takeScreenShot(View view) {
        Bitmap screenShot = null;


        try {
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();

            // config -->
            // Bitmap.Config.ARGB_8888
            // Bitmap.Config.ARGB_4444
            // Bitmap.Config.RGB_565
            screenShot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(screenShot);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            // Bitmap.createBitmap --> out of memory
            // NullPointerException
            //Toast.makeText(this, "takeScreenShot(): Exception", Toast.LENGTH_LONG).show();

            // BAD to catch (Exception e), just keep it now
            // bad online example
            // https://www.youtube.com/watch?v=nJ5Wu4XyzbY

            makeTextAndShow(this, "takeScreenShot(): Exception", Toast.LENGTH_LONG);

            return screenShot;
        }

        // temp msg
        //Toast.makeText(this, "view.draw(canvas) OK", Toast.LENGTH_LONG).show();
        //makeTextAndShow(this, "view.draw(canvas) OK", Toast.LENGTH_SHORT);

        return screenShot;
    }

    private void saveScreenShot(Bitmap screenShot) {
        ByteArrayOutputStream byteArrayOutStream = null;
        File file = null;
        String errMsg = "";

        try {
            byteArrayOutStream = new ByteArrayOutputStream();
            // Bitmap.CompressFormat.JPEG
            // Bitmap.CompressFormat.PNG
            // Bitmap.CompressFormat.WEBP

            /*
                         * public boolean compress (Bitmap.CompressFormat format, int quality, OutputStream stream)
                         * quality -->
                         * Hint to the compressor, 0-100.
                         * 0 meaning compress for small size, 100 meaning compress for max quality.
                         * Some formats, like PNG which is lossless, will ignore the quality setting
                         */
            screenShot.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, byteArrayOutStream);

            // Environment.getExternalStorageDirectory() --> SD card ?
            String fileName = Environment.getExternalStorageDirectory() + File.separator + "player.png";
            //String fileName = Environment.getExternalStorageDirectory() + "/" + "player.png";
            errMsg = fileName;

            file = new File(fileName);
            Toast.makeText(this, fileName, Toast.LENGTH_LONG).show();

            // throws IOException

            if ( file.createNewFile() ) {
                makeTextAndShow(this, "New file: " + fileName, Toast.LENGTH_SHORT);
            } else {
                // The file already exists.
                makeTextAndShow(this, "File already exists: " + fileName, Toast.LENGTH_SHORT);

            } // otherwise, IOException
            errMsg = "createNewFile() OK";


            FileOutputStream fileOutStream = new FileOutputStream(file);
            fileOutStream.write(byteArrayOutStream.toByteArray());
            errMsg = "write() OK";
            fileOutStream.close();

        } catch (FileNotFoundException e) {
            // never in, if createNewFile() fail, go to IOException
            // if createNewFile() OK, then file should exist

            e.printStackTrace();
            // new FileOutputStream(...);

            makeTextAndShow(this, "saveScreenShot(): FileNotFoundException ", Toast.LENGTH_LONG);

        } catch (IOException e) {
            // BAD to catch (Exception e)
            e.printStackTrace();

            // file.createNewFile() --> Permission denied
            // fileOutStream.write(...)
            // fileOutStream.close()

            makeTextAndShow(this, "saveScreenShot(): IOException, " + errMsg, Toast.LENGTH_LONG);

        }
    }
}
