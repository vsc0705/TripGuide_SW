package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private String receiverUserId, senderUserId;
    String profileback_download_url, feed_uri;

    private Button sendMessageRequestButton, addToWishlistButton;
    RecyclerView profile_feed;
    private CircleImageView ivUser;
    private ImageView ivBack;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    TextView name;
    TextView keyword;
    TextView location;
    TextView language;
    TextView introduce;

    String profile_language;
    Intent intent;
    TextView language_sub;

    //이미지 부분



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name=findViewById(R.id.profile_name);
        keyword=findViewById(R.id.profile_keyword);
        location=findViewById(R.id.profile_location);
        language=findViewById(R.id.profile_language);
        language_sub=findViewById(R.id.profile_language_sub);
        introduce=findViewById(R.id.profile_introduce);

        //startday=(TextView)findViewById(R.id.profileActivity_starting_date);
        //endday=(TextView)findViewById(R.id.profileActivity_end_date);

        db= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        intent=getIntent();

        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = intent.getExtras().get("visitUserId").toString();


        sendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        addToWishlistButton = findViewById(R.id.add_to_wishlist_button);
        sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendChatRequest();
            }
        });
        addToWishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWishList();
            }
        });
        ivUser=findViewById(R.id.profile_ivUser);
        ivBack=findViewById(R.id.profile_ivUserBackground);

        // if 문에서 이미 매칭된 경우에도 버튼 제거 필요
        if(senderUserId.equals(receiverUserId)){
            sendMessageRequestButton.setEnabled(false);
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }

        db.collection("Users").document(receiverUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    private void RetrieveUserInfo() {

        db.collection("Users").document(receiverUserId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
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

                            /*if(profile_map.containsKey("AnswerDate_start")) {
                                startDate = document.getDate("AnswerDate_start");
                                SimpleDateFormat startTimeFormat = new SimpleDateFormat("yyyy년 MM월  dd일 E요일 ");
                                startTimeFormat.format(startDate);
                                startday.setText("Answer from  " + startTimeFormat.format(startDate) + "~");
                            }
                            if(profile_map.containsKey("AnswerDate_end")) {
                                endDate = document.getDate("AnswerDate_end");
                                SimpleDateFormat endTimeformat = new SimpleDateFormat("yyyy년 MM월  dd일 E요일 ");
                                endTimeformat.format(endDate);
                                endday.setText("To  " + endTimeformat.format(endDate));
                            }*/

                            if(profile_map.containsKey("status")){
                                String profile_status = profile_map.get("status").toString();
                                introduce.setText(profile_status);
                            }
                            if(profile_map.containsKey("newL")){
                                String L=(String)profile_map.get("newL");
                                if(L.equals("English"))
                                    profile_language= "Main : "+L;
                                if(L.equals("Korean"))
                                    profile_language= "Main : "+L;
                                if(L.equals("Chinese"))
                                    profile_language= "Main : "+L;
                                language.setText(profile_language);
                            }
                            if(profile_map.containsKey("language")){
                                HashMap<String,Boolean> langlist=(HashMap)profile_map.get("language");
                                String profile_language_sub="";

                                for(String userlang:langlist.keySet()) {
                                    profile_language_sub= profile_language_sub + userlang+", ";
                                }
                                language_sub.setText("Sub : " +profile_language_sub);
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
        );

        db.collection("Users").document(senderUserId).collection("Matching")
                .document(receiverUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> requestinfo;
                    requestinfo = task.getResult().getData();
//                    Log.i("debug", String.valueOf(requestinfo));
                    if(requestinfo != null){
                        if(requestinfo.containsKey("sent")){
                            sendMessageRequestButton.setText(R.string.cancel_invite);
                        }
                    }
                }

            }

        });
    }
    private void SendChatRequest() {

        if(sendMessageRequestButton.getText().equals(getString(R.string.cancel_invite))){
            Map<String,Object> removesent = new HashMap<>();
            removesent.put("sent", FieldValue.delete());
            //removesent.put("ismatched", false);
            db.collection("Users").document(senderUserId).collection("Matching")
                    .document(receiverUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Map<String,Object> removereceived = new HashMap<>();
                        removereceived.put("received", FieldValue.delete());
                        //removereceived.put("ismatched", false);
                        db.collection("Users").document(receiverUserId)
                                .collection("Matching").document(senderUserId).update(removereceived);
                    }
                }
            });
            sendMessageRequestButton.setText(R.string.add_friend);
        } else{
            Map<String, Object> requestInfo_send = new HashMap<>();
            requestInfo_send.put("sent", true);
            requestInfo_send.put("ismatched", false);
            db.collection("Users").document(senderUserId).collection("Matching").document(receiverUserId).set(requestInfo_send, SetOptions.merge()).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Map<String, Object> requestInfo_receive = new HashMap<>();
                                requestInfo_receive.put("received", true);
                                requestInfo_receive.put("ismatched", false);
                                db.collection("Users").document(receiverUserId)
                                        .collection("Matching").document(senderUserId)
                                        .set(requestInfo_receive, SetOptions.merge());
                                sendFCM(receiverUserId);
                                sendMessageRequestButton.setText(R.string.cancel_invite);
                            }
                        }
                    }
            );
        }
    }

    private void addToWishList() {
        //String 처리 필요
        if(addToWishlistButton.getText().equals(getString(R.string.remove_wishlist))){
            db.collection("Users").document(senderUserId).collection("Wishlist").document(receiverUserId).delete();
            addToWishlistButton.setText(R.string.add_to_wishlist);
        } else{
            Map<String, Object> wishlist_info = new HashMap<>();
            wishlist_info.put("time", FieldValue.serverTimestamp());
            db.collection("Users").document(senderUserId).collection("Wishlist").document(receiverUserId).set(wishlist_info, SetOptions.merge());
            addToWishlistButton.setText(R.string.remove_wishlist);
        }
    }

    private void sendFCM(final String receiverId){
        //Log.d(TAG, senderName + "님이 " + task.getResult().get("name").toString() + "님께 매칭요청 전송");
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAst3LJCQ:APA91bFXxaAjnupdToP6oUYp8qXK8akknY5EKOo-8_ZXURJ64zraxbV27OnKrMhIaQm9hKx4JcPqtQRvl1_O6xbob-xv66WEvXFrV7wzLAXHsJA_tt1RTXXLP7v-9fXq6BXQsliEPFT4";
        db.collection("Users").document(senderUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final String senderName = task.getResult().get("name").toString();
                db.collection("Users").document(receiverId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        final String receiverFCMId = task.getResult().get("FCMToken").toString();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //JSON 메시지 생성
                                    JSONObject pushRoot = new JSONObject();
                                    JSONObject pushMsg = new JSONObject();
                                    JSONObject pushData = new JSONObject();
                                    pushMsg.put("body", senderName+"님이 매칭을 요청하셨습니다.");
                                    pushMsg.put("title", "새 매칭 요청");
                                    pushData.put("pushType", "request");
                                    pushData.put("requestUserId", senderUserId);
                                    pushRoot.put("notification", pushMsg);
                                    pushRoot.put("to", receiverFCMId);
                                    pushRoot.put("data", pushData);
                                    Log.d(TAG, "onComplete: "+pushRoot.toString());
                                    //POST 방식으로 FCM 서버에 전송
                                    URL Url = new URL(FCM_MESSAGE_URL);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("Content-type", "application/json");
                                    OutputStream os = conn.getOutputStream();
                                    os.write(pushRoot.toString().getBytes("utf-8"));
                                    os.flush();
                                    conn.getResponseCode();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        });

    }
    public void onStart(){
        super.onStart();
        FirestoreRecyclerOptions<Feed> options =new FirestoreRecyclerOptions.Builder<Feed>()
                .setQuery(db.collection("Feeds").whereEqualTo("uid",receiverUserId),Feed.class).build();

        FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position, @NonNull Feed model) {
                        db.collection("Feeds").whereEqualTo("uid",receiverUserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userId = getSnapshots().getSnapshot(position).get("uid").toString();
                                String feedId= getSnapshots().getSnapshot(position).getId();
                                Intent profileIntent = new Intent(getApplication(), FeedDetailActivity.class);
                                profileIntent.putExtra("userId", userId);
                                profileIntent.putExtra("feedId", feedId);
                                startActivity(profileIntent);
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
