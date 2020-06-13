package com.example.trip2.ui.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.Contacts;
import com.example.trip2.ProfileActivity;
import com.example.trip2.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecondActivity extends AppCompatActivity implements Serializable {

    Button btn_next;
    private RecyclerView findUserRecyclerList;
    private CollectionReference usersRef;
    private FirebaseFirestore db;
   FirestoreRecyclerAdapter fsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
       // HashMap<String,Boolean> getLanguages=(HashMap<String, Boolean>)intent.getSerializableExtra("Languages");
        //HashMap<String,Boolean> getInterests=(HashMap<String, Boolean>)intent.getSerializableExtra("Interests");
        HashMap<String, Date> getDate=(HashMap<String, Date>)intent.getSerializableExtra("tripdate");
        String getLanguages=intent.getExtras().getString("Languages");
        //ArrayList<String> getInterests=intent.getStringArrayListExtra("Interests");
        //String[] array = getInterests.toArray(new String[getInterests.size()]);

        HashMap<String,Boolean> getLocations=(HashMap<String, Boolean>)intent.getSerializableExtra("Locations");


        btn_next=(Button)findViewById(R.id.btn_next);


        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");
        findUserRecyclerList = (RecyclerView)findViewById(R.id.findUser_recycler_list);
        findUserRecyclerList.setLayoutManager(new LinearLayoutManager(this));

          Query setting = usersRef.whereEqualTo("location",getLocations).whereEqualTo("question",false).whereEqualTo("newL",getLanguages);

        //나중에 여기 변경해야 list 세팅에 맞게 뜸 collection query 확인 할것
        //리사이클러뷰 어댑터를 filterable을 implements 해서 만들면 필터링 기능 사용할듯함
        //현재 이미 매칭된 사람도 리스트에 뜨는 문제가 있는데, 필터링 기능과 함께 수정 필요함
        FirestoreRecyclerOptions<Contacts> fsOptions = new FirestoreRecyclerOptions.Builder<Contacts>()
                .setQuery(setting, Contacts.class).build();//  User 전체가 반환되는 쿼리


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
                        Picasso.get().load(model.getUser_image())
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .resize(0,170)
                                .into(holder.profileImage);

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
