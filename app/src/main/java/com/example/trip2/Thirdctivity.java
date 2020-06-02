package com.example.trip2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.trip2.ui.home.FeedWriteFragment;

import java.io.ByteArrayOutputStream;

public class Thirdctivity extends AppCompatActivity {
    //여기

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;
    ImageButton btn_change;
    ImageButton btn_ok;

    byte[] byteArray;
    Bitmap bitmap;
    ByteArrayOutputStream stream;


    //여기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdctivity);

        //여기
        imageview = (ImageView) findViewById(R.id.image);



        btn_change = (ImageButton) findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });


        btn_ok = (ImageButton) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bitmap = ((BitmapDrawable)imageview.getDrawable()).getBitmap();
                FeedWriteFragment fragment = new FeedWriteFragment();



                Bundle bundle = new Bundle();
                bundle.putParcelable("image", bitmap);
                bundle.putString("test","@@@");
                fragment.setArguments(bundle);


                finish();


            }
        });
        //여기


    }

    //여기

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
        }
    }

    //여기
}
