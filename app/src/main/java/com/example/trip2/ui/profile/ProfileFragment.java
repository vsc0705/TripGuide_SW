package com.example.trip2.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trip2.R;

public class ProfileFragment extends Fragment {




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        GridView grid = (GridView) view.findViewById(R.id.grid_view);//중요
        grid.setAdapter(new ImageAdapter(getActivity()));//중요
        return view;
    }
}