package com.example.trip2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trip2.R;
import com.example.trip2.SelectionActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EvaluationFragment extends Fragment {
    RatingBar point;
    EditText uiuncom, help, manuuncom, price, etc ;
    Button submit;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);

        point = (RatingBar) view.findViewById(R.id.rating);

        uiuncom = (EditText) view.findViewById(R.id.uiuncom);
        help = (EditText) view.findViewById(R.id.help);
        manuuncom = (EditText) view.findViewById(R.id.manuuncom);
        price = (EditText) view.findViewById(R.id.price);
        etc = (EditText) view.findViewById(R.id.etc);

        submit = (Button) view.findViewById(R.id.btn_submit);

        db = FirebaseFirestore.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evalUser();
                Intent intent = new Intent(getContext(), SelectionActivity.class);
                startActivity(intent);
            }});
        return view;
    }
    private void evalUser(){
        DocumentReference evalDoc = db.collection("app_Evaluation").document();
        String evalDocId = evalDoc.getId();
        HashMap evalInfo = new HashMap();
        evalInfo.put("rating", Float.toString(point.getRating()));
        evalInfo.put("help section",help.getText().toString());
        evalInfo.put("price limit", price.getText().toString());
        evalInfo.put("UIuncomfortable", uiuncom.getText().toString());
        evalInfo.put("manual uncomfortable", manuuncom.getText().toString());
        evalInfo.put("etc", etc.getText().toString());
        db.collection("app_Evaluation").document(evalDocId).set(evalInfo);

    }
}
