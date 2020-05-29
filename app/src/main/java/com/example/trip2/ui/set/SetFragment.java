package com.example.trip2.ui.set;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.trip2.MainActivity;
import com.example.trip2.R;
import com.example.trip2.fragment_select;
import com.example.trip2.responer_matching_list;

public class SetFragment extends Fragment {
    TextView textView_startdate, textView_enddate;
    Button btn_start, btn_end;
    //오추가
    Button btn_ok;


    public static SetFragment newInstance(){
        return new SetFragment();
    }
    //오추가

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_set, container, false);
        textView_startdate=view.findViewById(R.id.textView_startdate);
        textView_enddate=view.findViewById(R.id.textView_enddate);
        btn_start=view.findViewById(R.id.btn_start);
        btn_end=view.findViewById(R.id.btn_end);

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

        //오추가
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).replaceFragment(responer_matching_list.newInstance());
            }
        });
        //오추가


        return view;
    }
    void showStartDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                textView_startdate.setText(year+"."+(month+1)+"."+dayOfMonth);

            }
        },2020, 5, 28);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }
    void showEndDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                textView_enddate.setText(year+"."+(month+1)+"."+dayOfMonth);

            }
        },2020, 5, 28);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }

}