package com.example.chung_che.myfirstapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by JimCC_Yu on 2015/10/2.
 * use code from
 * http://vinsol.com/blog/2014/10/01/handling-back-button-press-inside-fragments/
 * NOT OK NOW~
 */

public abstract class BackHandledFragment extends Fragment {
    protected BackHandlerInterface backHandlerInterface;
    public abstract String getTagText();
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity()  instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Mark this fragment as the selected Fragment.
        backHandlerInterface.setSelectedFragment(this);
    }

    public interface BackHandlerInterface {
        public void setSelectedFragment(BackHandledFragment backHandledFragment);
    }
}

