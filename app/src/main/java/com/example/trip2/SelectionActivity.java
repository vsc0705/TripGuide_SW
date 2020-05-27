package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {
    ImageButton question;
    ImageButton respondent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        question= (ImageButton) findViewById(R.id.selection_questioner);
        respondent=(ImageButton)findViewById(R.id.selection_respondent);

        respondent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent question_home = new Intent(SelectionActivity.this, MainActivity.class);
                startActivity(question_home);
            }
        });
    }
}
