package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class selection extends AppCompatActivity {
    ImageButton questioner;
    ImageButton respondent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        questioner= (ImageButton) findViewById(R.id.selection_questioner);
        respondent=(ImageButton)findViewById(R.id.selection_respondent);


        questioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent questioner_home= new Intent(selection.this, questioner_main.class);
                startActivity(questioner_home);
            }
        });
        respondent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent respondent_home = new Intent(selection.this, MainActivity.class);
                startActivity(respondent_home);
            }
        });
    }
}
