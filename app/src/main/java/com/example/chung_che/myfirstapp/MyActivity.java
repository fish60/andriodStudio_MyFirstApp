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
import java.io.FileInputStream;
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
    private final static String SCREENSHOT_DEFAULT_FILE_NAME = "_temp_screenshot_temp_.png";

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
            case R.id.action_file:
                callActivityFile();
                return true;
            case R.id.action_draw:
                callActivityDraw();
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

                    // need catch IOException...
                    // and this implementation do nothing ?????
                    //openedStream.close();

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
    public void takeScreenshotAndSave(View view) {
        // take screenshot, also save it

        // Only get the button, care view input
        //Bitmap screenshot = takeScreenshot(view);
        Bitmap screenshot = null;

        int id = view.getId();


        switch (id) {
            case R.id.buttonScreenshot1:
                makeTextAndShow(this, "buttonScreenshot 1", Toast.LENGTH_SHORT);
                // This works, use this (AppCompatActivity is an Activity) --> getWindow() -->
                screenshot = takeScreenshot(this.getWindow().getDecorView());

                break;
            case R.id.buttonScreenshot2:
                makeTextAndShow(this, "buttonScreenshot 2", Toast.LENGTH_SHORT);
                //Bitmap screenshot2 = takeScreenshot2(view);
                screenshot = takeScreenshot2(this.getWindow().getDecorView());
                break;


            default:


        }

        if ( screenshot == null ) {
            makeTextAndShow(this, "No screenshot", Toast.LENGTH_SHORT);
            return;
        }

        // show it first
        mViewScreenshot.setImageBitmap(screenshot);

        saveScreenshotInternal(screenshot);


        //saveScreenshot(screenshot);
        // show ?

    }
    private Bitmap takeScreenshot2(View view) {
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

    private Bitmap takeScreenshot(View view) {
        Bitmap screenshot = null;


        try {
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();

            // config -->
            // Bitmap.Config.ARGB_8888
            // Bitmap.Config.ARGB_4444
            // Bitmap.Config.RGB_565
            screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(screenshot);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            // Bitmap.createBitmap --> out of memory
            // NullPointerException
            //Toast.makeText(this, "takeScreenshot(): Exception", Toast.LENGTH_LONG).show();

            // BAD to catch (Exception e), just keep it now
            // bad online example
            // https://www.youtube.com/watch?v=nJ5Wu4XyzbY

            makeTextAndShow(this, "takeScreenshot(): Exception", Toast.LENGTH_LONG);

            return screenshot;
        }

        // temp msg
        //Toast.makeText(this, "view.draw(canvas) OK", Toast.LENGTH_LONG).show();
        //makeTextAndShow(this, "view.draw(canvas) OK", Toast.LENGTH_SHORT);

        return screenshot;
    }

    // Environment.getExternalStorageDirectory() --> not internal, is external
    private void saveScreenshot(Bitmap screenshot) {
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
            screenshot.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, byteArrayOutStream);

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

            makeTextAndShow(this, "saveScreenshot(): FileNotFoundException ", Toast.LENGTH_LONG);

        } catch (IOException e) {
            // BAD to catch (Exception e)
            e.printStackTrace();

            // file.createNewFile() --> Permission denied
            // fileOutStream.write(...)
            // fileOutStream.close()

            makeTextAndShow(this, "saveScreenshot(): IOException, " + errMsg, Toast.LENGTH_LONG);

        }
    }

    // do this using a service in the future
    private void saveScreenshotInternal(Bitmap screenshot) {

        ByteArrayOutputStream byteArrayOutStream = null;

        // if we use openFileOutput(), only need string
        File file = null;


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
            screenshot.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, byteArrayOutStream);

            //file = new File(defaultFileName);

            // throws IOException
            //if ( file.createNewFile() ) {
            //    //makeTextAndShow(this, "New file: " + fileName, Toast.LENGTH_SHORT);
            //} else {
            //    // The file already exists.
            //    //makeTextAndShow(this, "File already exists: " + fileName, Toast.LENGTH_SHORT);
            //} // otherwise, IOException

            // http://developer.android.com/reference/java/io/FileOutputStream.html
            // no need to createNewFile(), but need new File()
            //FileOutputStream fileOutStream = new FileOutputStream(file);
            //fileOutStream.write(byteArrayOutStream.toByteArray());
            //fileOutStream.close();

            FileOutputStream fos = openFileOutput(SCREENSHOT_DEFAULT_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(byteArrayOutStream.toByteArray());
            fos.close();

            // 注意位置, 這樣只有關到部份, 程式碼要重購 ?????
            // 但其實是空的行動......卻有丟出 IOException 的可能
            byteArrayOutStream.close();


        } catch (FileNotFoundException e) {
            // never in, if createNewFile() fail, go to IOException
            // if createNewFile() OK, then file should exist

            // FileOutputStream()
            // if file cannot be opened for writing

            e.printStackTrace();
            // new FileOutputStream(...);


            makeTextAndShow(this, "saveScreenshotInternal(): FileNotFoundException ", Toast.LENGTH_LONG);

        } catch (IOException e) {
            // BAD to catch (Exception e)
            e.printStackTrace();

            // file.createNewFile() --> Permission denied
            // fileOutStream.write(...)
            // fileOutStream.close()

            // byteArrayOutStream.close();

            makeTextAndShow(this, "saveScreenshotInternal(): IOException", Toast.LENGTH_LONG);

        }




    }

    // save bitmap to internal storage
    private void _test_saveScreenshotInternal(Bitmap screenshot) {
        File fileInternal = getFilesDir();
        String fileNameDir = fileInternal.getAbsolutePath();

        // ex: /data/data/com.example.chung_che.myfirstapp/files
        //makeTextAndShow(this, fileInternal.getAbsolutePath(), Toast.LENGTH_LONG);

        // /data/data/com.example.chung_che.myfirstapp/files
        //makeTextAndShow(this, fileInternal.getPath(), Toast.LENGTH_LONG);

        // files
        //makeTextAndShow(this, fileInternal.getName(), Toast.LENGTH_LONG);

        String FILENAME = "hello_file";
        String string = "hello world!";
        try {

            for (int i=0; i<10; i++) {

                // create dir
                File subDir = getDir(( (Integer) i ).toString(), MODE_PRIVATE);

                //if ( subDir.mkdir() ) {
                //if ( subDir.getParentFile().mkdir() ) {
                //    //
                //    Toast.makeText(this, "mkdir: " + subDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                //} else {
                //    // ... so we have dir but can not get names
                //    Toast.makeText(this, "mkdir: " + subDir.getAbsolutePath() + " false", Toast.LENGTH_LONG).show();
                //}

                // maybe: /data/data/com.example.chung_che.myfirstapp/files/0
                // --> /data/data/com.example.chung_che.myfirstapp/app_0
                //makeTextAndShow(this, subDir.getAbsolutePath(), Toast.LENGTH_LONG);


                // use new File() to create dir
                // this is: /data/data/com.example.chung_che.myfirstapp/files/0
                // File subDir = new File(getFilesDir(), ( (Integer) i ).toString());
                //File subDir = new File(fileInternal, ( (Integer) i ).toString());
                //Toast.makeText(this, subDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                // use this will cause /data/data/com.example.chung_che.myfirstapp/files/0/hello_file0: open failed: ENOENT (No such file or directory)
                // in the following code, may need to make dir ??? but the name is not the name
                //if ( subDir.delete() ) {
                //    //
                //    Toast.makeText(this, "Delete: " + subDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                //} else {
                //    // ...
                //    Toast.makeText(this, "Delete: " + subDir.getAbsolutePath() + " failed", Toast.LENGTH_LONG).show();
                //}



                // file name: 0/hello_file, 1/hello_file ...
                // file name: 0/hello_file0, 1/hello_file1 ...
                //String fileName = ( (Integer) i ).toString() + File.separator + FILENAME;
                String fileName = subDir.getAbsolutePath() + File.separator +
                        FILENAME + ( (Integer) i ).toString();

                File fileInSubDir = new File(fileName);

                // java.lang.IllegalArgumentException: File 0/hello_file contains a path separator
                //FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);

                FileOutputStream fos = new FileOutputStream(fileInSubDir);
                //fos.write(string.getBytes());
                fos.close();

            }

            String[] fileNames = fileList();
            // only 1
            //makeTextAndShow(this, ((Integer)fileNames.length).toString(), Toast.LENGTH_LONG);

            // hello_file, but this is the old one
            //makeTextAndShow(this, fileNames[0], Toast.LENGTH_LONG);

            // still 1, still hello_file
            fileNames = fileInternal.list();
            makeTextAndShow(this, ((Integer) fileNames.length).toString(), Toast.LENGTH_LONG);
            //makeTextAndShow(this, fileNames[0], Toast.LENGTH_LONG);


            // /data/data/com.example.chung_che.myfirstapp/app_0
            File _subDir = getDir("0", MODE_PRIVATE);
            fileNames = _subDir.list();

            // say 0 --> /data/data/com.example.chung_che.myfirstapp/app_0
            //makeTextAndShow(this, _subDir.getAbsolutePath(), Toast.LENGTH_LONG);

            // 2, we have hello_file and hello_file0, YES
            // BUT we can not get dir name
            //makeTextAndShow(this, ((Integer)fileNames.length).toString(), Toast.LENGTH_LONG);

            //makeTextAndShow(this, fileNames[0], Toast.LENGTH_LONG);
            //makeTextAndShow(this, fileNames[1], Toast.LENGTH_LONG);


            // check this one can be delete with true
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();

            File lister = fileInternal.getAbsoluteFile();
            for ( String list : lister.list() )
            {
                //
                Toast.makeText(this, list, Toast.LENGTH_LONG).show();
                //File fileToBeDel = new File(list);
                //boolean delRes = fileToBeDel.delete();
                boolean delRes = this.deleteFile(list);
                Toast.makeText(this, "fileInternal -- delRes: " + ((Boolean)delRes).toString() + " for " + list, Toast.LENGTH_LONG).show();
            }

            // maybe:

            for (int i=0; i<10; i++) {
                File subDir = getDir(( (Integer) i ).toString(), MODE_PRIVATE);
                for ( String list : subDir.list() )
                {
                    //
                    Toast.makeText(this, list, Toast.LENGTH_LONG).show();
                    File fileToBeDel = new File(list);
                    boolean delRes = fileToBeDel.delete();
                    Toast.makeText(this, "delRes: " + ((Boolean)delRes).toString() + " for " + list, Toast.LENGTH_LONG).show();
                }
                boolean delRes = subDir.delete();
                Toast.makeText(this, "delRes: " + ((Boolean)delRes).toString() + " for " + subDir.getName(), Toast.LENGTH_LONG).show();

                // false...
                //boolean delRes = this.deleteFile(subDir.getName());
                //Toast.makeText(this, "delRes: " + ((Boolean)delRes).toString() + " for " + subDir.getName(), Toast.LENGTH_LONG).show();
            }


        } catch (FileNotFoundException e) {
            // never in, if createNewFile() fail, go to IOException
            // if createNewFile() OK, then file should exist

            e.printStackTrace();
            // new FileOutputStream(...);

            //makeTextAndShow(this, "saveScreenshot(): FileNotFoundException ", Toast.LENGTH_LONG);

        } catch (IOException e) {
            // BAD to catch (Exception e)
            e.printStackTrace();

            // file.createNewFile() --> Permission denied
            // fileOutStream.write(...)
            // fileOutStream.close()

            makeTextAndShow(this, "saveScreenshot(): IOException", Toast.LENGTH_LONG);

        }


    }

    // button, need public
    public void loadTempScreenshot(View view) {
        //
        _loadTempScreenshot();
    }

    // check if file exist, if yes, load it
    // file is generated by pressing Screenshot 1 / Screenshot 2
    // only 1 single file can exist now
    private void _loadTempScreenshot() {
        // check if file exist, if yes, load it
        FileInputStream inputStream = null;

        try {

            inputStream = openFileInput(SCREENSHOT_DEFAULT_FILE_NAME);
            mSelectedImage = BitmapFactory.decodeStream(inputStream);

            // set image to a view
            mViewScreenshot.setImageBitmap(mSelectedImage);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // file not found, that's ok
            // just do nothing
            makeTextAndShow(this, "No temp file now", Toast.LENGTH_LONG);
        }


    }

    private void callActivityFile() {
        Intent intent = new Intent(this, FileActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void callActivityDraw() {
        // TODO
        // add input image as background
        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
    }

}
