package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {

    private RecyclerView findFriendsRecyclerList;
    private DatabaseReference usersRef;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter fsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        db = FirebaseFirestore.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        findFriendsRecyclerList = (RecyclerView)findViewById(R.id.find_friends_recycler_list);
        findFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<Contacts> fsOptions = new FirestoreRecyclerOptions.Builder<Contacts>()
                .setQuery(db.collection("Users"), Contacts.class).build();

        fsAdapter = new
                FirestoreRecyclerAdapter<Contacts, FindFriendViewHolder>(fsOptions){

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.user_display_layout,viewGroup, false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        String visitUserId = getSnapshots().getSnapshot(position).getId();
                        Intent profileIntent = new Intent(FindFriendActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("visitUserId", visitUserId);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        findFriendsRecyclerList.setAdapter(fsAdapter);


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

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            itemView.findViewById(R.id.user_online_status).setVisibility(View.INVISIBLE);
        }
    }
}
