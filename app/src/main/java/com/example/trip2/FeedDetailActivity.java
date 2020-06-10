package com.example.trip2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedDetailActivity extends Activity {
    FirebaseFirestore db;
    private static final String TAG = "FeedDetailActivity";
    CircleImageView cvUser;
    ImageView ivFeed;
    LikeButton btnLike;
    private String currentUserID;
    private FirebaseAuth mAuth;
    TextView tvUser, time, tvFeedArea,tvDesc,tvLikeNum;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feed_detail);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        cvUser=findViewById(R.id.user_profile_image);
        tvUser=findViewById(R.id.user_profile_name);
        time=findViewById(R.id.user_time);

        ivFeed=findViewById(R.id.user_feed_image);
        tvFeedArea=findViewById(R.id.feed_area);
        tvDesc=findViewById(R.id.user_feed_desc);
        btnLike=findViewById(R.id.btn_like);
        tvLikeNum=findViewById(R.id.tv_likeNum);

        intent=getIntent();
        Log.d(TAG, "유저 아이디: "+intent.getExtras().get("userId").toString());
        Log.d(TAG, "피드 아이디: "+intent.getExtras().get("feedId").toString());
    }
    public void onStart(){
        super.onStart();
        db.collection("Users").document(intent.getExtras().get("userId").toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            //이름
                            tvUser.setText(task.getResult().get("name").toString());
                            //프로필 사진
                            if(task.getResult().contains("user_image")){
                                Picasso.get().load(task.getResult().get("user_image").toString())
                                        .placeholder(R.drawable.default_profile_image)
                                        .error(R.drawable.default_profile_image)
                                        .resize(0,70)
                                        .into(cvUser);
                            }
                        }
                    }
                });
        db.collection("Feeds").document(intent.getExtras().get("feedId").toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Timestamp timestamp=task.getResult()
                                    .getTimestamp("feed_time", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                            SimpleDateFormat sdf=new SimpleDateFormat("MMM dd EEE", Locale.ENGLISH);
                            String feedtime=sdf.format(timestamp.toDate());
                            time.setText(feedtime);
                            tvDesc.setText(task.getResult().get("feed_desc").toString());

                            if (task.getResult().contains("feed_uri")){
                                Picasso.get().load(task.getResult().get("feed_uri").toString())
                                        .placeholder(R.drawable.default_profile_image)
                                        .error(R.drawable.default_profile_image)
                                        .resize(0,250)
                                        .into(ivFeed);
                            }
                            task.getResult().getReference().collection("LikeMember").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            String like=task.getResult().size()+"";
                                            tvLikeNum.setText(like);
                                        }
                                    });
                            task.getResult().getReference().collection("LikeMember").document(currentUserID).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists()){
                                                btnLike.setLiked(true);
                                            }
                                            else {
                                                btnLike.setLiked(false);
                                            }
                                        }
                                    });
                            if(task.getResult().contains("feed_area")){
                                HashMap<String, Boolean> feedarea=(HashMap)task.getResult().getData().get("feed_area");
                                String feed_area_result="";

                                for (String feed_area_Elemet:feedarea.keySet()){
                                    feed_area_result=feed_area_result+"#"+feed_area_Elemet+" ";
                                }
                                tvFeedArea.setText(feed_area_result);
                            }
                            btnLike.setOnLikeListener(new OnLikeListener() {
                                @Override
                                public void liked(LikeButton likeButton) {
                                    String currentNum=tvLikeNum.getText().toString();
                                    int intCurrentNum=Integer.parseInt(currentNum)+1;
                                    tvLikeNum.setText(intCurrentNum+"");

                                    HashMap<String, Object> update_user_data=new HashMap<>();
                                    update_user_data.put("pushDate", new Timestamp(new Date()));
                                    update_user_data.put("uid",currentUserID);
                                    update_user_data.put("feed_uri",task.getResult().get("feed_uri").toString());
                                    task.getResult().getReference().collection("LikeMember")
                                            .document(currentUserID).set(update_user_data);
                                }

                                @Override
                                public void unLiked(LikeButton likeButton) {
                                    String currentNum=tvLikeNum.getText().toString();
                                    int intCurrentNum=Integer.parseInt(currentNum)-1;
                                    tvLikeNum.setText(intCurrentNum+"");

                                    task.getResult().getReference().collection("LikeMember")
                                            .document(currentUserID).delete();
                                }
                            });
                        }
                    }
                });
    }

    public void goProfile(View v){

        Intent goProfile=new Intent(getApplication(), OtherProfileActivity.class);
        goProfile.putExtra("userId",intent.getExtras().get("userId").toString());
        startActivity(goProfile);

        finish();
    }


    public void goCancel(View v){
        finish();
    }

}