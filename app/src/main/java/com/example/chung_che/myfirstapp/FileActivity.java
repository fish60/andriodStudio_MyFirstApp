package com.example.chung_che.myfirstapp;


import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;





//public class FileActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {
public class FileActivity extends AppCompatActivity {

    private final String FULL_SCREEN = "fullScreen";
    private final String LAYOUT_IN_SCREEN = "layoutInScreen";

    //private BackHandledFragment selectedFragment;

    //private long DELAY_SHOWTIME = 5000;

    // Handler mHandler --> static Handler sHandler
    private static Handler sHandler = null;

    // 還沒被生出來
    private ActionBar mActionBar = null;


    // 可以減少宣告, 但為了 removeCallbacks() 還是宣告變數
    // Runnable mRunnableShowActionBar --> static Runnable sRunnableShowActionBar
    private static Runnable sRunnableShowActionBar = null;


    private boolean mIsFlagFullScreen = false;
    private boolean mIsFlagLayoutInScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if ( savedInstanceState != null ) {
            // 保持狀態
            // ex: 全銀幕, 隱藏 status bar
            mIsFlagFullScreen = savedInstanceState.getBoolean(FULL_SCREEN);
            mIsFlagLayoutInScreen = savedInstanceState.getBoolean(LAYOUT_IN_SCREEN);

            if ( mIsFlagFullScreen && mIsFlagLayoutInScreen ) {
                // all set or all clear ?
                hideSystemUI();
            }

            // TODO
            // action bar

        }


        if ( sRunnableShowActionBar == null) {
            sRunnableShowActionBar = new Runnable() {
                @Override
                public void run() {
                    //showSystemUI();
                    showActionBar();

                }
            };
        }

        if ( sHandler == null ) {
            sHandler = new Handler();
        }

        mActionBar = getSupportActionBar();

        // 測試, 在這裡 remove 則 rotate 應該不會看到訊息
        // ... 有訊息, 應該是因為 重新 new mHandler 的關係
        // ... mRunnableShowActionBar 也是嗎?????
        // --> 請用 static 不然都會被重新 new
        sHandler.removeCallbacks(sRunnableShowActionBar);

        // TODO
        // 注意離開沒有 remove 找時機做 remove



        setContentView(R.layout.activity_file);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file, menu);




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 21
        Toast.makeText(this, "Build.VERSION.SDK_INT: " + ((Integer)Build.VERSION.SDK_INT).toString(), Toast.LENGTH_LONG).show();


        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:

                hideSystemUI();

                mActionBar.hide();

                if ( sHandler != null ) {
                    sHandler.postDelayed(sRunnableShowActionBar, 5000);

                    // 但是不好結束
                    /*
                    mHandler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    showSystemUI();
                                }
                            }, 2000
                    );
                    */

                }



                return true;
            case R.id.action_createTestDir:
                _createDirInternal("Test");
                return true;

            // let fragment to do it
            // so return false
            case R.id.action_refreshList:
                //_refreshDirList();
                //return false;
                //return super.onOptionsItemSelected(item);
            default:
        }



        return super.onOptionsItemSelected(item);
    }


    /*
    @Override
    public void onBackPressed() {
        if(selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }
    */

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //savedInstanceState.putInt(CURRENT_ACTIONBAR_FLAG, mActionbarFlag);

        int windowAttributeFlag = getWindow().getAttributes().flags;
        mIsFlagFullScreen =
                (windowAttributeFlag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        == WindowManager.LayoutParams.FLAG_FULLSCREEN;
        savedInstanceState.putBoolean(FULL_SCREEN, mIsFlagFullScreen);

        mIsFlagLayoutInScreen =
                (windowAttributeFlag & WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
                        == WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        savedInstanceState.putBoolean(LAYOUT_IN_SCREEN, mIsFlagLayoutInScreen);


    }

    private void _createDirInternal(String dirName) {

        File fileInternal = getFilesDir();
        // /data/data/com.example.chung_che.myfirstapp/files
        String fileNameDir = fileInternal.getAbsolutePath();

        //String fileName = subDir.getAbsolutePath() + File.separator +
        //        FILENAME + ( (Integer) i ).toString();

        // /data/data/com.example.chung_che.myfirstapp/app_DIRNAME( NO . )
        File subDir = getDir(dirName, MODE_PRIVATE);

        File checkSubDir = new File(subDir.getAbsolutePath());

        if ( checkSubDir.exists() ) {
            Toast.makeText(this, checkSubDir.getAbsolutePath() + " exists", Toast.LENGTH_LONG).show();

            Toast.makeText(this, "checkSubDir.delete(): " + ((Boolean)checkSubDir.delete()).toString(), Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(this, checkSubDir.getAbsolutePath() + " does not exist", Toast.LENGTH_LONG).show();
        }

        if ( checkSubDir.mkdir() ) {
            Toast.makeText(this, "mkdir: " + checkSubDir.getAbsolutePath() + " true", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "isDirectory(): " + ((Boolean)checkSubDir.isDirectory()).toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "isHidden(): " + ((Boolean)checkSubDir.isHidden()).toString(), Toast.LENGTH_LONG).show();

        } else {
            // ... so we have dir but can not get names
            Toast.makeText(this, "mkdir: " + checkSubDir.getAbsolutePath() + " false", Toast.LENGTH_LONG).show();
        }

        File lister = fileInternal.getAbsoluteFile();
        for ( String list : lister.list() ) {
            Toast.makeText(this, list, Toast.LENGTH_LONG).show();
            //File fileToBeDel = new File(list);
            //boolean delRes = fileToBeDel.delete();
            //boolean delRes = this.deleteFile(list);
            //Toast.makeText(this, "fileInternal -- delRes: " + ((Boolean)delRes).toString() + " for " + list, Toast.LENGTH_LONG).show();
        }
        for ( File file: lister.listFiles() ) {
            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            //File fileToBeDel = new File(list);
            //boolean delRes = fileToBeDel.delete();
            //boolean delRes = this.deleteFile(list);
            //Toast.makeText(this, "fileInternal -- delRes: " + ((Boolean)delRes).toString() + " for " + list, Toast.LENGTH_LONG).show();
            if (file.isDirectory()) {
                Toast.makeText(this, "isDirectory()", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "not dir", Toast.LENGTH_LONG).show();
            }
        }

        // /data/data/com.example.chung_che.myfirstapp
        // but not only user data, but also other files / dirs, be careful
        lister = fileInternal.getParentFile();
        for ( File file: lister.listFiles() ) {
            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            //File fileToBeDel = new File(list);
            //boolean delRes = fileToBeDel.delete();
            //boolean delRes = this.deleteFile(list);
            //Toast.makeText(this, "fileInternal -- delRes: " + ((Boolean)delRes).toString() + " for " + list, Toast.LENGTH_LONG).show();
            if (file.isDirectory()) {
                Toast.makeText(this, "isDirectory()", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "not dir", Toast.LENGTH_LONG).show();
            }
        }

        // get app_ files
        File[] files = lister.listFiles(
                new FilenameFilter() {
                    public boolean accept(File f, String fName) {
                        return fName.toLowerCase().startsWith("app_");
                    }
                }
            );
        for ( File file: files ) {
            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            //File fileToBeDel = new File(list);
            //boolean delRes = fileToBeDel.delete();
            //boolean delRes = this.deleteFile(list);
            //Toast.makeText(this, "fileInternal -- delRes: " + ((Boolean)delRes).toString() + " for " + list, Toast.LENGTH_LONG).show();
            if (file.isDirectory()) {
                Toast.makeText(this, "isDirectory()", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "not dir", Toast.LENGTH_LONG).show();
            }
        }

    }


    // communicate with fragment
    // no need to do it
    private void _refreshDirList() {
        // http://developer.android.com/reference/android/app/FragmentManager.html

        // version
        //FragmentManager fragmentManager = getFragmentManager();

        // use Support
        FragmentManager fragmentManager = getSupportFragmentManager();

        // do the cast
        FileActivityFragment fragment = (FileActivityFragment) fragmentManager.findFragmentById(R.id.fragment);

        if ( fragment.isAdded() ) {
            fragment.refreshList();
        }



    }


    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.

        /* need 11but 8
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        */

        // hind status bar, but 可以刷回來
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // use full screen 包括 status bar 下面的地方
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);


    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        /* /* need 11but 8
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        */
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // remove mRunnableShowActionBar 不然快速多次呼叫可能很歡樂?
        sHandler.removeCallbacks(sRunnableShowActionBar);
    }

    private void showActionBar() {
        // remove mRunnableShowActionBar 不然快速多次呼叫可能很歡樂?
        //mHandler.removeCallbacks(mRunnableShowActionBar);
        mActionBar.show();

        // 會出現, 表示 postDelayed() 丟進去後 rotation 並沒有被清掉
        Toast.makeText(this, "showActionBar(), check if it can be shown for rotation", Toast.LENGTH_LONG).show();

    }


}
