package com.example.trip2.ui.list;

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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.ChatActivity;
import com.example.trip2.Contacts;
import com.example.trip2.PicassoTransformations;
import com.example.trip2.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class List_Fragment extends Fragment {
    private static final String TAG = "List_Fragment";
    private View privateChatsView;
    private RecyclerView chatsList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter fsAdapter;

    private DatabaseReference chatsRef, usersRef;
    private String currentUserId;
    private StorageReference mStorageRef;
    File localFile;

    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    String stEmail;
    String username,userstatus,user_uri;
    public List_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        privateChatsView =  inflater.inflate(R.layout.fragment_list, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = (RecyclerView)privateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

//사진 관련 코드
        SharedPreferences sharedPref = getActivity().getSharedPreferences("shared", Context.MODE_PRIVATE);
        stEmail=sharedPref.getString("email","");
        Log.d(TAG, "stEmail: "+stEmail);

        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }
        //
        return privateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirestoreRecyclerOptions<Contacts> options = new FirestoreRecyclerOptions.Builder<Contacts>()
                .setQuery(db.collection("Users").document(currentUserId).collection("Matching").whereEqualTo("ismatched", true), Contacts.class).build();

//        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
//                .setQuery(chatsRef, Contacts.class)
//                .build();
        FirestoreRecyclerAdapter<Contacts, ChatsViewHolder> fsAdapter =
                new FirestoreRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String userId = getSnapshots().getSnapshot(position).getId();
                        DocumentReference docRef = getSnapshots().getSnapshot(position).getReference();
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            username = task.getResult().get("name").toString();
                                            userstatus=task.getResult().get("status").toString();
//                                            db.disableNetwork();
                                            if(task.getResult().contains("user_image")){
                                                user_uri=task.getResult().get("user_image").toString();
                                                PicassoTransformations.targetWidth=70;
                                                Picasso.get().load(user_uri)
                                                        .placeholder(R.drawable.default_profile_image)
                                                        .error(R.drawable.default_profile_image)

                                                        .transform(PicassoTransformations.resizeTransformation)
                                                        .into(holder.profileImage);
                                            }
                                            holder.userName.setText(username);
                                            holder.userStatus.setText(userstatus);



                                            if(task.getResult().get("state").toString().equals("true")) {
                                                holder.userOnlineStatus.setImageResource(R.drawable.online);
                                            } else {
                                                String date = task.getResult().get("date").toString();
                                                //holder.userStatus.setText("Last Active\n"+ date);
                                                holder.userOnlineStatus.setImageResource(R.drawable.offline);
                                            }


                                        }
                                    }
                                });

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                        chatIntent.putExtra("visitUserId", userId);
                                        chatIntent.putExtra("visitUserName", username);
                                        //chatIntent.putExtra("visitUserImage", localFile.getAbsolutePath());
                                        startActivity(chatIntent);
                                    }
                                });
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

//        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
//                        final String userIds = getRef(position).getKey();
//
//                        usersRef.child(userIds).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                try {
//                                    localFile = File.createTempFile("images", "jpg");
//                                    StorageReference riversRef = mStorageRef.child("users").child(stEmail).child("profile.jpg");
//                                    riversRef.getFile(localFile)
//                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                                @Override
//                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                                    // Successfully downloaded data to local file
//                                                    // ...
//                                                    Picasso.get().load(localFile.getAbsolutePath()).placeholder(R.drawable.profile_image);
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception exception) {
//                                            // Handle failed download
//                                            // ...
//                                        }
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//
//                                final String userName = dataSnapshot.child("name").getValue().toString();
//                                holder.userName.setText(userName);
//
//                                if(dataSnapshot.child("userState").hasChild("state")){
//                                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
//                                    String date = dataSnapshot.child("userState").child("date").getValue().toString();
//                                    String time = dataSnapshot.child("userState").child("time").getValue().toString();
//
//                                    if(state.equals("online")){
//                                        holder.userStatus.setText("Online");
//                                        holder.userOnlineStatus.setImageResource(R.drawable.online);
//                                    }
//                                    else if(state.equals("offline")){
//                                        holder.userStatus.setText("Last Active\n"+ date + " " + time);
//                                        holder.userOnlineStatus.setImageResource(R.drawable.offline);
//                                    }
//                                }
//                                else{
//                                    holder.userStatus.setText("Offline");
//                                }
//
//
//                                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
//                                        chatIntent.putExtra("visitUserId", userIds);
//                                        chatIntent.putExtra("visitUserName", userName);
//                                        chatIntent.putExtra("visitUserImage", localFile.getAbsolutePath());
//                                        startActivity(chatIntent);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
//                        return new ChatsViewHolder(view);
//                    }
//                };
        chatsList.setAdapter(fsAdapter);
        fsAdapter.startListening();
    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView userName,userStatus;
        ImageView userOnlineStatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            userOnlineStatus = itemView.findViewById(R.id.user_online_status);

        }
    }
}