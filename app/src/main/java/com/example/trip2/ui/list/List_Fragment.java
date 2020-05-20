package com.example.trip2.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;

import com.example.trip2.R;

public class List_Fragment extends ListFragment {

    ListViewAdapter adapter ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adapter = new ListViewAdapter() ;
        setListAdapter(adapter) ;

        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_account_box_black_36dp),
                "John", "where") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_account_box_black_36dp),
                "Jimmy", "bye~~") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_account_box_black_36dp),
                "Tom", "hi~") ;

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}