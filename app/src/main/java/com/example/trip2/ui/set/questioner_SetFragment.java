package com.example.trip2.ui.set;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trip2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

public class questioner_SetFragment extends Fragment
{
    TextView textView_startdate, textView_enddate;
    Button questions_btn_next;
    String questions_startday;
    String questions_endday;
    String questions_Languages="";
    LinearLayout Q_Ll_start, Q_Ll_end;

    private CheckBox restaurant, culture, show, art, sights, shopping, walk;
    private LinearLayout Q_Kangwon, Q_Gyeonggi, Q_South_Gyeongsang, Q_North_Gyeongsang, Q_Kwangju, Q_Daegu, Q_Daejeon, Q_Busan, Q_Seoul, Q_Sejong, Q_Ulsan, Q_Incheon, Q_South_Jeolla
            , Q_North_jeolla,Q_Jeju, Q_South_Chungcheong, Q_North_Chungcheoung ;
    private Spinner location;

    private RadioButton questions_r1, questions_r2, questions_r3;

    private String currentUserID;
    private FirebaseAuth mAuth;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;
    private OkHttpClient client=new OkHttpClient();


    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_questioner_set, container, false);
        textView_startdate=view.findViewById(R.id.textView_startdate);
        textView_enddate=view.findViewById(R.id.textView_enddate);
        Q_Ll_start=view.findViewById(R.id.Q_Ll_start);
        Q_Ll_end=view.findViewById(R.id.Q_Ll_end);
        questions_btn_next=view.findViewById(R.id.question_next);

        Q_Ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDate();
            }
        });
        Q_Ll_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDate();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

        location=view.findViewById(R.id.questioner_location);

        questions_r1=view.findViewById(R.id.questioner_set_english);
        questions_r2=view.findViewById(R.id.questioner_set_korean);
        questions_r3=view.findViewById(R.id.questioner_set_chinese);

        restaurant=view.findViewById(R.id.question_restaurant);
        culture =view.findViewById(R.id.question_culture);
        show=view.findViewById(R.id.question_show);
        art=view.findViewById(R.id.question_art);
        sights=view.findViewById(R.id.question_sights);
        shopping=view.findViewById(R.id.question_shopping);
        walk=view.findViewById(R.id.question_walk);

        //지역 관련
        Q_Kangwon = view.findViewById(R.id.Q_Kangwon);
        Q_Gyeonggi = view.findViewById(R.id.Q_Gyeonggi);
        Q_South_Gyeongsang = view.findViewById(R.id.Q_South_Gyeongsang);
        Q_North_Gyeongsang = view.findViewById(R.id.Q_North_Gyeongsang);
        Q_Kwangju = view.findViewById(R.id.Q_Kwangju);
        Q_Daegu = view.findViewById(R.id.Q_Daegu);
        Q_Daejeon = view.findViewById(R.id.Q_Daejeon);
        Q_Busan = view.findViewById(R.id.Q_Busan);
        Q_Seoul = view.findViewById(R.id.Q_Seoul);
        Q_Sejong = view.findViewById(R.id.Q_Sejong);
        Q_Ulsan = view.findViewById(R.id.Q_Ulsan);
        Q_Incheon = view.findViewById(R.id.Q_Incheon);
        Q_South_Jeolla = view.findViewById(R.id.Q_South_Jeolla);
        Q_North_jeolla = view.findViewById(R.id.Q_North_jeolla);
        Q_Jeju = view.findViewById(R.id.Q_Jeju);
        Q_South_Chungcheong = view.findViewById(R.id.Q_South_Chungcheong);
        Q_North_Chungcheoung = view.findViewById(R.id.Q_North_Chungcheoung);

        String[] cityarray = getResources().getStringArray(R.array.city);
        final ArrayAdapter adapter = new ArrayAdapter(this.getContext(),R.layout.support_simple_spinner_dropdown_item, cityarray);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        location.setSelection(0);
        location.setAdapter(adapter);
        //이미지 클릭으로 스피너 값 변경

        context=container.getContext();



        Q_Kangwon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(0);
                Toast.makeText(context,"You select Kang-won-do.",Toast.LENGTH_SHORT).show();
            }
        });

        Q_Gyeonggi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(1);
                Toast.makeText(context,"You select Gyeong-gi-do.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_South_Gyeongsang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(2);
                Toast.makeText(context,"You select South_Gyeong-sang-do.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_North_Gyeongsang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(3);
                Toast.makeText(context,"You select North_Gyeong-sang-do.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Kwangju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(4);
                Toast.makeText(context,"You select Kwang-ju.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Daegu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(5);
                Toast.makeText(context,"You select Dae-gu.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Daejeon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(6);
                Toast.makeText(context,"You select Dae-jeon.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Busan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(7);
                Toast.makeText(context,"You select Busan.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Seoul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(8);
                Toast.makeText(context,"You select Seo-ul.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Sejong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(9);
                Toast.makeText(context,"You select Se-jong.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Ulsan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(10);
                Toast.makeText(context,"You select Ul-san.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Incheon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(11);
                Toast.makeText(context,"You select In-cheon.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_South_Jeolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(12);
                Toast.makeText(context,"You select South_Jeol-la-do.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_North_jeolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(13);
                Toast.makeText(context,"You select Nouth_Jeol-la-do.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_Jeju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(14);
                Toast.makeText(context,"You select Je-ju.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_South_Chungcheong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(15);
                Toast.makeText(context,"You select South_Chung-cheong-do.",Toast.LENGTH_SHORT).show();
            }
        });
        Q_North_Chungcheoung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setSelection(16);
                Toast.makeText(context,"You select Nouth_Chung-cheong-do.",Toast.LENGTH_SHORT).show();
            }
        });



        questions_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UpdateSettings();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                catch (java.lang.NullPointerException e){
                    Toast.makeText(context,"Check your tripdate",Toast.LENGTH_SHORT).show();
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

                questions_startday=year+"."+(month+1)+"."+dayOfMonth;

                textView_startdate.setText(questions_startday);

            }
        },2020, 5, 28);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }
    void showEndDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                questions_endday=year+"."+(month+1)+"."+dayOfMonth;

                textView_enddate.setText(questions_endday);

            }
        },2020, 5, 28);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }

    private void UpdateSettings() throws ParseException {


        String setStartday= questions_startday;
        String setEndday=questions_endday;

        final List<String> questions_Interests=new ArrayList<>();

        final HashMap<String,Boolean> questions_locations=new HashMap<>();


        /* DocumentReference questions_del=db.collection("Users").document(currentUserID);
        Map<String, Object> questions_delete=new HashMap<>();

         questions_delete.put("question_date", FieldValue.delete());

         questions_del.update(questions_delete).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {

             }
         });*/
/*
       final ArrayList<Date> question_tripdate;
       final ArrayList<String> question_Interests;
       final ArrayList<String> question_Languages;

        question_tripdate = new ArrayList<>();
       question_Interests = new ArrayList<>();
        question_Languages = new ArrayList<>();
*/
        if(questions_r1.isChecked())
            questions_Languages="English";
        if(questions_r2.isChecked())
            questions_Languages="Korean";
        if(questions_r3.isChecked())
            questions_Languages="Chinese";

        if(restaurant.isChecked())
            questions_Interests.add("restaurant");
        if(culture.isChecked())
            questions_Interests.add("culture");
        if(show.isChecked())
            questions_Interests.add("show");
        if(art.isChecked())
            questions_Interests.add("art");
        if(sights.isChecked())
            questions_Interests.add("sights");
        if(shopping.isChecked())
            questions_Interests.add("shopping");
        if(walk.isChecked())
            questions_Interests.add("walk");

        questions_locations.put(location.getSelectedItem().toString(),true);

        SimpleDateFormat fm = new SimpleDateFormat("yyyy.MM.dd");

        Date question_start = fm.parse(setStartday);
        Date question_end = fm.parse(setEndday);

        if(question_start.after(question_end))
        {
            Toast.makeText(context,"날짜 입력을 확인하세요.",Toast.LENGTH_SHORT).show();
            return ;
        }
        final HashMap<String,Date> questions_tripdate=new HashMap<>();

        /*question_tripdate.add(question_start);
        question_tripdate.add(question_end);*/

        questions_tripdate.put("start",question_start);
        questions_tripdate.put("end",question_end);

        HashMap<String, HashMap> question_setMap = new HashMap<>();


        question_setMap.put("question_date", questions_tripdate);


        /*question_setMap.put("QuestionDay",question_tripdate);*/
        //profileMap.put("user_keyword", setKeyword);

        db.collection("Users").document(currentUserID).set(question_setMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(context, "MatchingSet Successful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), SecondActivity.class);
                   /* intent.putExtra("Interests",question_Interests);
                    intent.putExtra("Languages",question_Languages);
                    intent.putExtra("tripdate",question_tripdate);*/
                   intent.putExtra("Languages",questions_Languages);
                   intent.putExtra("Interests",questions_Interests.toArray());
                   intent.putExtra("tripdate",questions_tripdate);
                   intent.putExtra("Locations",questions_locations);
                    startActivity(intent);

                } else {
                    String message = task.getException().toString();
                    //Toast.makeText(MainActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
