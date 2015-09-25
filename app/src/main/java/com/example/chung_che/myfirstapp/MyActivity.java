package com.example.chung_che.myfirstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

/*
 * 注意輸入的字何時會消失何時會保存
 * 按左上的 back 與左下硬體預設 back 有差
 * 連中圖預覽都會不一致... 如果在 focus 下 左下 back 就是預覽有, 進入無
 * 想辦法處理? (正確的系統結果但是使用者會覺得怪)
 * 與 restore 有關???
 */

public class MyActivity extends AppCompatActivity {
    private final static int IMAGE_GALLERY = 100;
    // "com.mycompany.myfirstapp.MESSAGE" --> change ???
    public final static String EXTRA_MESSAGE = "com.example.chung_che.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
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

        if ( requestCode == IMAGE_GALLERY ) {
            // response from image gallery
            Uri picDir = data.getData();

            try {
                InputStream openedStream = getContentResolver().openInputStream(picDir);
            } catch ( FileNotFoundException e) {
                // 確認 Toast
                Toast.makeText(this, getString(R.string.msg_unableToOpenImage), Toast.LENGTH_LONG).show();

                // test for commit
            }



        }
    }


    // try to figure out the input View view

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
    public void openSend() {
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

}
