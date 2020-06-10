package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherProfileActivity extends AppCompatActivity {
    private static final String TAG = "OtherProfileActivity";
    String profileback_download_url, feed_uri;

    RecyclerView profile_feed;

    private String userID;
    private FirebaseAuth mAuth;

    private CircleImageView ivUser;
    private ImageView ivBack;

    FirebaseFirestore db;

    //TextView 부분
    TextView name;
    TextView keyword;
    TextView location;
    TextView language;
    TextView introduce;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        name=findViewById(R.id.profile_name);
        keyword=findViewById(R.id.profile_keyword);
        location=findViewById(R.id.profile_location);
        language=findViewById(R.id.profile_language);
        introduce=findViewById(R.id.profile_introduce);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        intent=getIntent();

        userID=intent.getExtras().get("userId").toString();

        ivUser=findViewById(R.id.profile_ivUser);
        ivBack=findViewById(R.id.profile_ivUserBackground);

        db.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists()){
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
                        if(imgMap.containsKey("user_back_image")){
                            final String backUri = imgMap.get("user_back_image").toString();
                            Picasso.get().load(backUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .resize(0,400)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(backUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,400)
                                                    .into(ivBack);
                                        }
                                    });
                        }
                    }
                }
            }
        });

        RetrieveUserInfo();

        profile_feed=(RecyclerView)findViewById(R.id.feed_list);
        GridLayoutManager proFeedGridManger=new GridLayoutManager(getApplication(),3);
        profile_feed.setLayoutManager(proFeedGridManger);

    }
    private void RetrieveUserInfo(){
        db.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        Map<String, Object> profile_map=document.getData();// 문서 전체를 profile_map으로 받아온것
                        if(profile_map.containsKey("name")) {
                            String profile_name = profile_map.get("name").toString();
                            name.setText(profile_name);
                        }
                        if(profile_map.containsKey("location")){
                            HashMap<String,Boolean> locationpart=(HashMap)profile_map.get("location");
                            String profile_location="";
                            for(String userlocation : locationpart.keySet())
                            {
                                profile_location=profile_location+userlocation;
                            }
                            location.setText(profile_location);
                        }
                        if(profile_map.containsKey("status")){
                            String profile_status = profile_map.get("status").toString();
                            introduce.setText(profile_status);
                        }
                        if(profile_map.containsKey("language")){
                            HashMap<String,Boolean> langlist=(HashMap)profile_map.get("language");
                            String profile_language="";
                            for(String userlang:langlist.keySet()) {

                                profile_language=profile_language+userlang+",  ";
                            }
                            language.setText(profile_language);
                        }

                        if(profile_map.containsKey("user_keyword")){

                            HashMap<String,Boolean> user_keywords=(HashMap)profile_map.get("user_keyword");
                            String profile_userkeyword="";

                            for(String userinterest:user_keywords.keySet()){

                                profile_userkeyword=profile_userkeyword+userinterest+",  ";
                            }
                            keyword.setText(profile_userkeyword);

                        }
                    }
                }
            }
        });
    }
    public void onStart(){
        super.onStart();
        FirestoreRecyclerOptions<Feed> options =new FirestoreRecyclerOptions.Builder<Feed>()
                .setQuery(db.collection("Feeds").whereEqualTo("uid",userID),Feed.class).build();

        FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position, @NonNull Feed model) {
                        db.collection("Feeds").whereEqualTo("uid",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().getDocuments().get(position).contains("feed_uri")){
                                        feed_uri=task.getResult().getDocuments().get(position).get("feed_uri").toString();
                                        Picasso.get().load(feed_uri)
                                                .placeholder(R.drawable.load)
                                                .error(R.drawable.load)
                                                .resize(0,200)
                                                .into(holder.feed);

                                    }
                                }
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.question_profile_feed, parent, false);
                        return new FeedViewHolder(view);
                    }
                };
        profile_feed.setAdapter(feedAdapter);
        feedAdapter.startListening();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        ImageView feed;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feed=itemView.findViewById(R.id.profile_feed);
        }
    }
}