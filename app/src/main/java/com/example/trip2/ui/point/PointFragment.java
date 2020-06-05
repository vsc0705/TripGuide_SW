package com.example.trip2.ui.point;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trip2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PointFragment extends Fragment {
    private static final String TAG = "POINTDEBUG";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserID;
    private long myPoints = 0;
    TextView textView_point;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        View view=inflater.inflate(R.layout.fragment_point, container, false);
        textView_point = view.findViewById(R.id.my_point);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(currentUserID).collection("Points").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.e(TAG, "성공");
                    for(DocumentSnapshot ds:task.getResult().getDocuments()){
                        Log.e(TAG, ds.get("point_get").toString());
                        myPoints = myPoints+(long) ds.get("point_get");
                    }
                    Log.e(TAG, "내 포인트"+myPoints);
                    textView_point.setText(String.valueOf(myPoints));
                }
            }
        });
        return view;
    }
}