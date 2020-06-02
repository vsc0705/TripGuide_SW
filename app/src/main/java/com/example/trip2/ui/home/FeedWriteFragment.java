package com.example.trip2.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trip2.R;
import com.example.trip2.Thirdctivity;

import java.io.ByteArrayOutputStream;

public class FeedWriteFragment extends Fragment {
    //여기
    Button change;
    ImageView imageview;

    Bitmap bitmap;
    Bundle bundle;

    String test;

    //여기

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feedwrite, container, false);
        //여기


        imageview = (ImageView) view.findViewById(R.id.feed_veiw);



        change = (Button) view.findViewById(R.id.btn_change_photo);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Thirdctivity.class);
                startActivity(intent);

            }
        });



        //여기


        return view;
    }
    //여기



    //여기

}
