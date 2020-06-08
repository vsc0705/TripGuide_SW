package com.example.trip2.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.Feed;
import com.example.trip2.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class questioner_profileFragment extends Fragment {
    private static final String TAG = "question_ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    String profileback_download_url;
    private StorageReference mStorageRef;

    RecyclerView profile_feed;

    private String currentUserID;
    private FirebaseAuth mAuth;

    private CircleImageView ivUser;
    private ImageView ivBack;


    TextView question_name;
    TextView question_keyword;
    TextView question_language;
    TextView question_location;
    TextView question_introduce;

    FirebaseFirestore db;

    public questioner_profileFragment(){}


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_questioner_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        currentUserID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        ivUser=view.findViewById(R.id.question_profile_ivUser);
        ivBack=view.findViewById(R.id.profile_ivUserBackground);

        question_name=view.findViewById(R.id.question_profile_name);
        question_keyword=view.findViewById(R.id.question_profile_keyword);
        question_location=view.findViewById(R.id.question_profile_location);
        question_language=view.findViewById(R.id.question_profile_language);
        question_introduce=view.findViewById(R.id.question_profile_introduce);

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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });


        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //db.disableNetwork();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_image")) {
                            final String userUri = imgMap.get("user_image").toString();
                            Picasso.get().load(userUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .resize(0,100)
                                    .into(ivUser, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,100)
                                                    .into(ivUser);
                                        }
                                    });
                        }
                    }
                }
            }
        });
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //db.disableNetwork();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> imgMap = document.getData();
                        if (imgMap.containsKey("user_back_image")) {
                            final String userbackUri = imgMap.get("user_back_image").toString();
                            Picasso.get().load(userbackUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                    .resize(0,200)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userbackUri)
                                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                                    .resize(0,200)
                                                    .into(ivBack);
                                        }
                                    });
                        }
                    }
                }
            }
        });
        RetrieveUserInfo();

        profile_feed=(RecyclerView)view.findViewById(R.id.feed_list);
        GridLayoutManager proFeedGridManger=new GridLayoutManager(getContext(),3);
        profile_feed.setLayoutManager(proFeedGridManger);

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void RetrieveUserInfo(){
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        Map<String, Object> profile_map=document.getData();
                        if(profile_map.containsKey("name")) {
                            String profile_name = profile_map.get("name").toString();
                            question_name.setText(profile_name);
                        }
                        if(profile_map.containsKey("location")){
                            HashMap<String,Boolean> locationpart=(HashMap)profile_map.get("location");
                            String profile_location="";
                            for(String userlocation : locationpart.keySet())
                            {
                                profile_location=profile_location+userlocation;
                            }
                            question_location.setText(profile_location);
                        }
                        if(profile_map.containsKey("status")){
                            String profile_status = profile_map.get("status").toString();
                            question_introduce.setText(profile_status);
                        }
                        if(profile_map.containsKey("language")){
                            HashMap<String,Boolean> langlist=(HashMap)profile_map.get("language");
                            String profile_language="";
                            for(String userlang:langlist.keySet()) {
                                profile_language=profile_language+userlang+",  ";
                            }
                            question_language.setText(profile_language);
                        }

                        if(profile_map.containsKey("user_keyword")){

                            HashMap<String,Boolean> user_keywords=(HashMap)profile_map.get("user_keyword");
                            String profile_userkeyword="";

                            for(String userinterest:user_keywords.keySet()){

                                profile_userkeyword=profile_userkeyword+userinterest+",  ";
                            }
                            question_keyword.setText(profile_userkeyword);

                        }
                    }
                }
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE && resultCode == getActivity().RESULT_OK){
            final Uri image=data.getData();
            Picasso.get().load(image)
                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                    .error(R.drawable.profile_ivuserbackgroundimage)
                    .resize(0,200)
                    .into(ivBack);

            final StorageReference riversRef = mStorageRef.child("Users").child(currentUserID).child("profile_back.jpg");
            UploadTask uploadTask=riversRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(getActivity(), "Profile Photo Error: " + task.getException().getMessage());
                    }
                    profileback_download_url=riversRef.getDownloadUrl().toString();
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        profileback_download_url=task.getResult().toString();

                        HashMap<String, Object> update_user_data=new HashMap<>();
                        update_user_data.put("user_back_image",profileback_download_url);

                        db.collection("Users").document(currentUserID).set(update_user_data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });


                    }
                }
            });
        }
    }
    public void onStart(){
        super.onStart();
        FirestoreRecyclerOptions<Feed> options =new FirestoreRecyclerOptions.Builder<Feed>()
        .setQuery(db.collection("Users").document(currentUserID).collection("LikeFeed"),Feed.class).build();

        final FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position, @NonNull final Feed model) {
                        db.collection("Users").document(currentUserID).collection("LikeFeed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                                String feed_uri=task.getResult().getDocuments().get(position).get("feed_uri").toString();
                                            Log.d(TAG, "받는 URI: "+feed_uri);
                                                Picasso.get().load(feed_uri)
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.load)
                                                        .resize(0,200)
                                                        .into(holder.feed);
                                        }

                                    }
                        });
                    }

                    @NonNull
                    @Override
                    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.question_profile_feed, parent, false);
                        return new FeedViewHolder(view);
                    }
                };
        profile_feed.setAdapter(feedAdapter);
        feedAdapter.startListening();
    }
    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        ImageView feed;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feed=itemView.findViewById(R.id.profile_feed);
        }
    }
}
