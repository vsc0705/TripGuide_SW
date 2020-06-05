package com.example.trip2.ui.set;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private LinearLayout Kangwon, Gyeonggi, South_Gyeongsang, North_Gyeongsang, Kwangju, Daegu, Daejeon, Busan, Seoul, Sejong, Ulsan, Incheon, South_Jeolla
    , North_jeolla,Jeju, South_Chungcheong, North_Chungcheoung ;

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




        english=view.findViewById(R.id.respondent_set_english);
        korean=view.findViewById(R.id.respondent_set_korean);

        restaurant=view.findViewById(R.id.respondent_set_restaurant);
        culture =view.findViewById(R.id.respondent_set_culture);
        show=view.findViewById(R.id.respondent_set_show);
        art=view.findViewById(R.id.respondent_set_art);
        sights=view.findViewById(R.id.respondent_set_sights);
        food=view.findViewById(R.id.respondent_set_food);
        walk=view.findViewById(R.id.respondent_set_walk);

        context=container.getContext();



        //지역 관련
        Kangwon = view.findViewById(R.id.kangwon);
        Gyeonggi = view.findViewById(R.id.Gyeonggi);
        South_Gyeongsang = view.findViewById(R.id.South_Gyeongsang);
        North_Gyeongsang = view.findViewById(R.id.North_Gyeongsang);
        Kwangju = view.findViewById(R.id.Kwangju);
        Daegu = view.findViewById(R.id.Daegu);
        Daejeon = view.findViewById(R.id.Daejeon);
        Busan = view.findViewById(R.id.Busan);
        Seoul = view.findViewById(R.id.Seoul);
        Sejong = view.findViewById(R.id.Sejong);
        Ulsan = view.findViewById(R.id.Ulsan);
        Incheon = view.findViewById(R.id.Incheon);
        South_Jeolla = view.findViewById(R.id.South_Jeolla);
        North_jeolla = view.findViewById(R.id.North_jeolla);
        Jeju = view.findViewById(R.id.Jeju);
        South_Chungcheong = view.findViewById(R.id.South_Chungcheong);
        North_Chungcheoung = view.findViewById(R.id.North_Chungcheoung);


        location=view.findViewById(R.id.respondent_set_location);
        String[] cityarray = getResources().getStringArray(R.array.city);


        final ArrayAdapter adapter = new ArrayAdapter(this.getContext(),R.layout.support_simple_spinner_dropdown_item, cityarray);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        location.setSelection(0);
        location.setAdapter(adapter);
        //이미지 클릭으로 스피너 값 변경


        Kangwon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(0);
                Toast.makeText(context,"강원도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        Gyeonggi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(1);
                Toast.makeText(context,"경기도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        South_Gyeongsang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(2);
                Toast.makeText(context,"경상남도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        North_Gyeongsang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(3);
                Toast.makeText(context,"경상북도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Kwangju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(4);
                Toast.makeText(context,"광주를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Daegu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(5);
                Toast.makeText(context,"대구를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Daejeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(6);
                Toast.makeText(context,"대전을 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Busan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(7);
                Toast.makeText(context,"부산을 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Seoul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(8);
                Toast.makeText(context,"서울을 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Sejong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(9);
                Toast.makeText(context,"세종을 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Ulsan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(10);
                Toast.makeText(context,"울산을 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Incheon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(11);
                Toast.makeText(context,"인천을 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        South_Jeolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(12);
                Toast.makeText(context,"전라남도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        North_jeolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(13);
                Toast.makeText(context,"전라북도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        Jeju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(14);
                Toast.makeText(context,"제주도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        South_Chungcheong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(15);
                Toast.makeText(context,"충청남도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        North_Chungcheoung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(16);
                Toast.makeText(context,"충청북도를 선택하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        

        RetrieveUserInfo();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UpdateSettings();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                catch (java.lang.NullPointerException e){
                    Toast.makeText(context,"please set your tripdate",Toast.LENGTH_SHORT).show();
                    return;
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

        HashMap<String, Date> tripdate = new HashMap<>();
        String setStartday= startday;
        String setEndday=endday;


        SimpleDateFormat fm = new SimpleDateFormat("yyyy.MM.dd");

        Date start = fm.parse(setStartday);
        Date end = fm.parse(setEndday);

        if(start.after(end))
        {
            Toast.makeText(context,"Check your trip date",Toast.LENGTH_SHORT).show();
            return ;
        }

        tripdate.put("start",start);
        tripdate.put("end",end);

        HashMap<String, HashMap> setMap = new HashMap<>();


            setMap.put("AnswerDate",tripdate);
            //profileMap.put("user_keyword", setKeyword);

            db.collection("Users").document(currentUserID).set(setMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(context, "매칭 설정을 완료했습니다.", Toast.LENGTH_SHORT).show();


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
                                    HashMap<String,Boolean> langlist=(HashMap)map.get("language");
                                    for(String userlang:langlist.keySet()){
                                        if(userlang.equals("English")) {
                                            english.setChecked(true);
                                        }
                                        if(userlang.equals("korean")){
                                            korean.setChecked(true);
                                        }
                                    }
                                }
                                if(map.containsKey("location")){
                                    HashMap<String,Boolean> locations=(HashMap)map.get("location");
                                    String[] cityarray = getResources().getStringArray(R.array.city);
                                    if(locations.containsValue(true)){
                                        for(String locationpart : locations.keySet()){
                                            for(int i=0; i<cityarray.length; i++){
                                                if(locationpart.equals(cityarray[i])){
                                                    location.setSelection(i);
                                                }
                                            }
                                        }
                                    }
                                }

                                if(map.containsKey("user_keyword")){
                                    HashMap<String,Boolean> user_keywords=(HashMap)map.get("user_keyword");
                                    for(String userinterest:user_keywords.keySet()){
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