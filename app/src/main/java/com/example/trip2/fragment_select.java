package com.example.trip2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_select extends Fragment {

    public fragment_select() {
        // Required empty public constructor
    }
    //@@@@@@@@@@@@@
    public static fragment_select newInstance(){
            return new fragment_select();
    }
    //@@@@@@@@@@@@@@@

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select, container, false);
    }
}
