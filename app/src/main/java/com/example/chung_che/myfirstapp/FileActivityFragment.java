package com.example.chung_che.myfirstapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
//public class FileActivityFragment extends Fragment {
public class FileActivityFragment extends ListFragment {
    // actually should be const
    private File INTERNAL_ROOT = null;
    private File mCurrentDir = null;

    private boolean mHasRefreshed = false;
    int mCurCheckPosition = 0;
    // 還不能用
    //View mDecorView = getActivity().getWindow().getDecorView();




    public FileActivityFragment() {
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);

        INTERNAL_ROOT = context.getFilesDir().getParentFile();
        mCurrentDir = INTERNAL_ROOT;

        Toast.makeText(getActivity(), "Root: " + INTERNAL_ROOT.getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // for handling menu
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filelist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] values = new String[] { "test1", "test2" };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, values);
        setListAdapter(adapter);


        if ( savedInstanceState != null ) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                String item = (String) getListAdapter().getItem(position);
                Toast.makeText( getActivity().getBaseContext()  , "Long Clicked " + item, Toast.LENGTH_SHORT).show();

                // will still call onListItemClick()
                //return false;

                // will not call onListItemClick()
                return true;
            }
        };
        getListView().setOnItemLongClickListener(listener);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(getActivity(), "F -- onOptionsItemSelected()", Toast.LENGTH_LONG).show();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refreshList:
                refreshList();
                return true;
            default:
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // based on current value of mCurrentDir
        // the list should be the right files, or it may cause wrong result

        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_LONG).show();

        if ( !mHasRefreshed || mCurrentDir == null ) {
            Toast.makeText(getActivity(), "Not ready yet", Toast.LENGTH_LONG).show();
            return;
        }

        File lister = mCurrentDir.getAbsoluteFile();

        File[] listFiles = lister.listFiles();
        if ( position >= listFiles.length ) {
            //
            Toast.makeText(getActivity(), "position >= listFiles.length", Toast.LENGTH_LONG).show();
            return;
        }
        if ( listFiles[position].isDirectory() ) {

            Toast.makeText(getActivity(), "Dir, let's go", Toast.LENGTH_LONG).show();
            mCurrentDir = listFiles[position];
            refreshList();

        } else {
            Toast.makeText(getActivity(), "Not a dir", Toast.LENGTH_LONG).show();
        }



    }




    public void refreshList() {
        // Toast.makeText(this.getActivity(), "refreshList()", Toast.LENGTH_LONG).show();

        if ( mCurrentDir == null ) {
            return;
        }


        File lister = mCurrentDir.getAbsoluteFile();
        String[] listFilesStr = lister.list();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, listFilesStr);

        setListAdapter(adapter);

        File[] listFiles = lister.listFiles();
        for ( int i=0; i<listFiles.length; i++ ) {
            //Toast.makeText(getActivity(), list, Toast.LENGTH_LONG).show();
            if ( listFiles[i].isDirectory() ) {
                listFilesStr[i] = listFilesStr[i] + " (Dir)";
            } else {
                int j = 1;
            }

        }

        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, listFilesStr);
        setListAdapter(adapter);

        mHasRefreshed = true;
    }




}
