package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecondActivity extends AppCompatActivity {

    Button btn_next;
    private RecyclerView findUserRecyclerList;
    //private DatabaseReference usersRef;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter fsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);



        btn_next=(Button)findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SecondActivity.this,Thirdctivity.class);
                startActivity(intent);
            }
        });


        db = FirebaseFirestore.getInstance();
       // usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        findUserRecyclerList = (RecyclerView)findViewById(R.id.findUser_recycler_list);
        findUserRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        FirestoreRecyclerOptions<Contacts> fsOptions = new FirestoreRecyclerOptions.Builder<Contacts>()
                .setQuery(db.collection("Users"), Contacts.class).build();

        fsAdapter = new
                FirestoreRecyclerAdapter<Contacts, FindUserViewHolder>(fsOptions){

                    @NonNull
                    @Override
                    public FindUserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext())
                                .inflate(R.layout.user_display_layout,viewGroup, false);
                        FindUserViewHolder viewHolder = new FindUserViewHolder(view);
                        return viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final FindUserViewHolder holder, final int position, @NonNull Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());


                        holder.itemView.setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                String visitUserId = getSnapshots().getSnapshot(position).getId();
                                Intent profileIntent = new Intent(SecondActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visitUserId", visitUserId);
                                startActivity(profileIntent);
                            }
                        });
                     }
        };
        findUserRecyclerList.setAdapter(fsAdapter);
    }

    @Override
    protected void onStop(){
        super.onStop();
        fsAdapter.stopListening();
    }
    @Override
    protected void onStart(){
        super.onStart();
        fsAdapter.startListening();
    }
    public static class FindUserViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindUserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            itemView.findViewById(R.id.user_online_status).setVisibility(View.INVISIBLE);
        }
    }



    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions<Contacts> options = new
//                FirebaseRecyclerOptions.Builder<Contacts>()
//                .setQuery(usersRef, Contacts.class)
//                .build();
//
//        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter = new
//                FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
//                        holder.userName.setText(model.getName());
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String visitUserId = getRef(position).getKey();
//                                Intent profileIntent = new Intent(FindFriendActivity.this, ProfileActivity.class);
//                                profileIntent.putExtra("visitUserId", visitUserId);
//                                startActivity(profileIntent);
//                            }
//                        });
//                    }
//
//                    @NonNull
//                    @Override
//                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//
//                        View view = LayoutInflater.from(viewGroup.getContext())
//                                .inflate(R.layout.user_display_layout,viewGroup, false);
//                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
//                        return viewHolder;
//
//                    }
//                };
//
//        findFriendsRecyclerList.setAdapter(adapter);
//        adapter.startListening();
//    }
}
