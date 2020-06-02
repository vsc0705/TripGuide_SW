package com.example.trip2.ui.set;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.trip2.MainActivity;
import com.example.trip2.R;
import com.example.trip2.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class SetFragment extends Fragment {
    TextView textView_startdate, textView_enddate;
    Button btn_start, btn_end, btn_next;
    String startday;
    String endday;

    private Button updateAccountSettings;
    private EditText userName,userStatus;
    private CheckBox english, korean, restaurant, culture, show, art, sights, food, walk;
    private Spinner location;

    private String currentUserID;
    private FirebaseAuth mAuth;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;
    private OkHttpClient client=new OkHttpClient();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_set, container, false);
        textView_startdate=view.findViewById(R.id.textView_startdate);
        textView_enddate=view.findViewById(R.id.textView_enddate);
        btn_start=view.findViewById(R.id.btn_start);
        btn_end=view.findViewById(R.id.btn_end);
        btn_next=view.findViewById(R.id.btn_next);

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

        userName = view.findViewById(R.id.set_user_name);
        userStatus = view.findViewById(R.id.set_profile_status);
        location=view.findViewById(R.id.respondent_set_location);

        english=view.findViewById(R.id.respondent_set_english);
        korean=view.findViewById(R.id.respondent_set_korean);

        restaurant=view.findViewById(R.id.respondent_set_restaurant);
        culture =view.findViewById(R.id.respondent_set_culture);
        show=view.findViewById(R.id.respondent_set_show);
        art=view.findViewById(R.id.respondent_set_art);
        sights=view.findViewById(R.id.respondent_set_sights);
        food=view.findViewById(R.id.respondent_set_food);
        walk=view.findViewById(R.id.respondent_set_walk);

        RetrieveUserInfo();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
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

    private void UpdateSettings() {

        List<String> tripdate = new ArrayList<>();
        String setStartday= startday;
        String setEndday=endday;
        tripdate.add(setStartday);
        tripdate.add(setEndday);

        HashMap<String, Object> setMap = new HashMap<>();


            setMap.put("AnswerDate",tripdate);
            //profileMap.put("user_keyword", setKeyword);

            db.collection("Users").document(currentUserID).set(setMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //SendUserToSelectActivity();
                        // Toast.makeText(SettingsActivity.this, "Profile Update Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        //Toast.makeText(MainActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }

    private void RetrieveUserInfo() {

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Map<String, Object> map = document.getData();
                                if(map.containsKey("name")){
                                    String retrieveUserName = map.get("name").toString();
                                    //userName.setText(retrieveUserName);
                                }
                                if(map.containsKey("status")){
                                    String retrieveUserStatus = map.get("status").toString();
                                    //userStatus.setText(retrieveUserStatus);
                                }
                                if(map.containsKey("language")){
                                    ArrayList<String> langlist = (ArrayList<String>) map.get("language");
                                    for(String userlang:langlist){
                                        if(userlang.equals("English")) {
                                            english.setChecked(true);
                                        }
                                        if(userlang.equals("한국어")){
                                            korean.setChecked(true);
                                        }
                                    }
                                }
                                if(map.containsKey("Interests")){
                                    ArrayList<String> interestlist = (ArrayList<String>) map.get("Interests");
                                    for(String userinterest:interestlist){
                                        if(userinterest.equals("restaurant")) {
                                            restaurant.setChecked(true);
                                        }
                                        if(userinterest.equals("culture")){
                                            culture.setChecked(true);
                                        }
                                        if(userinterest.equals("show")){
                                            show.setChecked(true);
                                        }
                                        if(userinterest.equals("art")){
                                            art.setChecked(true);
                                        }
                                        if(userinterest.equals("sights")){
                                            sights.setChecked(true);
                                        }
                                        if(userinterest.equals("food")){
                                            food.setChecked(true);
                                        }
                                        if(userinterest.equals("walk")){
                                            walk.setChecked(true);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
        );



    }

}