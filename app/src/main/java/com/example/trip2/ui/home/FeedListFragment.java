package com.example.trip2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.feed;
import com.example.trip2.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FeedListFragment extends Fragment {
    private static final String TAG = "PidListFragment";
    private View view;
    private FirebaseAuth mAuth;
    RecyclerView feedList;
    private FirebaseFirestore db;
    private String currentUserId;

    private String username,user_uri,photo_uri, pid_desc;

    public FeedListFragment(){

    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        view=inflater.inflate(R.layout.fragment_feed, container, false);
        feedList=(RecyclerView)view.findViewById(R.id.feed_list);
        feedList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

}
