package com.example.trip2;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Rating;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class evaluation extends AppCompatActivity {

    RatingBar r1, r2, r3;
    EditText opinion;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        r1=(RatingBar)findViewById(R.id.evaluation_firstStar);
        r2=(RatingBar)findViewById(R.id.evaluation_secondStar);
        r3=(RatingBar)findViewById(R.id.evaluation_thirdStar);

        opinion=(EditText)findViewById(R.id.evaluation_opinion);
        submit = (Button)findViewById(R.id.evaluation_submit);


    }
}