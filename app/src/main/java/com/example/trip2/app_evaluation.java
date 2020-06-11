package com.example.trip2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class app_evaluation extends AppCompatActivity {

    RatingBar point;
    EditText uiuncom, help, manuuncom, price, etc ;
    Button submit;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_evaluation);

        point = (RatingBar) findViewById(R.id.rating);

        uiuncom = (EditText) findViewById(R.id.uiuncom);
        help = (EditText) findViewById(R.id.help);
        manuuncom = (EditText) findViewById(R.id.manuuncom);
        price = (EditText) findViewById(R.id.price);
        etc = (EditText) findViewById(R.id.etc);

        submit = (Button) findViewById(R.id.btn_submit);
        db = FirebaseFirestore.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evalUser();
                Intent intent = new Intent(app_evaluation.this, LoginActivity.class);
                startActivity(intent);
            }});
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