package com.example.trip2.ui.set;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trip2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

public class questioner_SetFragment extends Fragment
{
    TextView textView_startdate, textView_enddate;
    Button btn_start, btn_end, btn_next;
    String startday;
    String endday;


    private EditText userName,userStatus;
    private CheckBox english, korean, restaurant, culture, show, art, sights, food, walk;
    private Spinner location;

    private String currentUserID;
    private FirebaseAuth mAuth;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;
    private OkHttpClient client=new OkHttpClient();

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_set, container, false);
        textView_startdate=view.findViewById(R.id.textView_startdate);
        textView_enddate=view.findViewById(R.id.textView_enddate);
        btn_start=view.findViewById(R.id.question_start);
        btn_end=view.findViewById(R.id.question_end);
        btn_next=view.findViewById(R.id.question_next);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDate();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDate();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

        location=view.findViewById(R.id.questioner_location);

        english=view.findViewById(R.id.question_english);
        korean=view.findViewById(R.id.question_korean);

        restaurant=view.findViewById(R.id.question_restaurant);
        culture =view.findViewById(R.id.question_culture);
        show=view.findViewById(R.id.question_show);
        art=view.findViewById(R.id.question_art);
        sights=view.findViewById(R.id.question_sights);
        food=view.findViewById(R.id.question_food);
        walk=view.findViewById(R.id.question_walk);

        context=container.getContext();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UpdateSettings();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        });

        return view;
    }
    void showStartDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                startday=year+"."+(month+1)+"."+dayOfMonth;

                textView_startdate.setText(startday);

            }
        },2020, 5, 28);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }
    void showEndDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endday=year+"."+(month+1)+"."+dayOfMonth;

                textView_enddate.setText(endday);

            }
        },2020, 5, 28);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }

    private void UpdateSettings() throws ParseException {

        String setStartday= startday;
        String setEndday=endday;

       final ArrayList<Date> tripdate;
       final ArrayList<String> Interests;
       final ArrayList<String> Languages;

        tripdate = new ArrayList<>();
       Interests = new ArrayList<>();
        Languages = new ArrayList<>();

        if(english.isChecked())
            Languages.add(english.getText().toString());
        if(korean.isChecked())
            Languages.add(korean.getText().toString());

        if(restaurant.isChecked())
            Interests.add(restaurant.getText().toString());
        if(culture.isChecked())
            Interests.add(culture.getText().toString());
        if(show.isChecked())
            Interests.add(show.getText().toString());
        if(art.isChecked())
            Interests.add(art.getText().toString());
        if(sights.isChecked())
            Interests.add(sights.getText().toString());
        if(food.isChecked())
            Interests.add(food.getText().toString());
        if(walk.isChecked())
            Interests.add(walk.getText().toString());


        SimpleDateFormat fm = new SimpleDateFormat("yyyy.MM.dd");

        Date start = fm.parse(setStartday);
        Date end = fm.parse(setEndday);

        tripdate.add(start);
        tripdate.add(end);

        HashMap<String, Object> question_setMap = new HashMap<>();


        question_setMap.put("QuestionDay",tripdate);
        //profileMap.put("user_keyword", setKeyword);

        db.collection("Users").document(currentUserID).set(question_setMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(context, "MatchingSet Successful please go back", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), SecondActivity.class);
                    /*intent.putExtra("Interests",Interests);
                    intent.putExtra("Languages",Languages);
                    intent.putExtra("tripdate",tripdate);*/
                    startActivity(intent);

                } else {
                    String message = task.getException().toString();
                    //Toast.makeText(MainActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
