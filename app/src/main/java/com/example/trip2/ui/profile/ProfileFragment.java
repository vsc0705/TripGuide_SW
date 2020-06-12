package com.example.trip2.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.Edits;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.trip2.DeleteFeedActivity;
import com.example.trip2.Feed;
import com.example.trip2.FeedDetailActivity;
import com.example.trip2.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    String profileback_download_url, feed_uri;
    private StorageReference mStorageRef;

    RecyclerView profile_feed;

    private String currentUserID;
    private FirebaseAuth mAuth;

    private CircleImageView ivUser;
    private ImageView ivBack;
    String profile_language="";



    FirebaseFirestore db;

    //TextView 부분
    TextView name;
    TextView keyword;
    TextView location;
    TextView language;
    TextView introduce;
    TextView startday;
    TextView endday;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name=view.findViewById(R.id.profile_name);
        keyword=view.findViewById(R.id.profile_keyword);
        location=view.findViewById(R.id.profile_location);
        language=view.findViewById(R.id.profile_language);
        introduce=view.findViewById(R.id.profile_introduce);
        db = FirebaseFirestore.getInstance();

        startday=view.findViewById(R.id.profile_start_date);
        endday=view.findViewById(R.id.profile_end_date);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        currentUserID=mAuth.getCurrentUser().getUid();

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

        ivUser=view.findViewById(R.id.profile_ivUser);
        ivBack=view.findViewById(R.id.profile_ivUserBackground);

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
                        if (imgMap.containsKey("user_back_image")) {
                            final String userbackUri = imgMap.get("user_back_image").toString();
                            Picasso.get().load(userbackUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                    .resize(0,400)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userbackUri)
                                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                                    .error(R.drawable.profile_ivuserbackgroundimage)
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

        profile_feed=(RecyclerView)view.findViewById(R.id.feed_list);
        GridLayoutManager proFeedGridManger=new GridLayoutManager(getContext(),3);
        profile_feed.setLayoutManager(proFeedGridManger);

        
        return view;

    }


    private void RetrieveUserInfo(){
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists())
                    {
                        Date startDate = document.getDate("AnswerDate_start");
                        Date endDate = document.getDate("AnswerDate_end");
                        SimpleDateFormat startTimeFormat = new SimpleDateFormat("yyyy년 MM월  dd일 E요일 ");
                        startTimeFormat.format(startDate);
                        SimpleDateFormat endTimeformat = new SimpleDateFormat("yyyy년 MM월  dd일 E요일 ");
                        endTimeformat.format(endDate);

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

                            if(profile_map.containsKey("AnswerDate_start"))
                                startday.setText("Answer day from"+startTimeFormat.format(startDate)+"~");
                            if(profile_map.containsKey("AnswerDate_end"))
                                endday.setText("To"+endTimeformat.format(endDate));
                        }
                        if(profile_map.containsKey("status")){
                            String profile_status = profile_map.get("status").toString();
                            introduce.setText(profile_status);
                        }
                        if(profile_map.containsKey("newL")){
                            String L=(String)profile_map.get("newL");
                            if(L.equals("English"))
                                profile_language="Main : "+L+"                     Sub : ";
                        }
                        if(profile_map.containsKey("language")){
                            HashMap<String,Boolean> langlist=(HashMap)profile_map.get("language");

                            for(String userlang:langlist.keySet()) {

                                profile_language=profile_language+userlang+", ";
                            }
                            language.setText(profile_language);
                        }

                        if(profile_map.containsKey("newI")){
                            List<String> profile_check =(ArrayList<String>)profile_map.get("newI");
                            String profile_newI="";
                            Iterator iterator=profile_check.iterator();
                            while(iterator.hasNext())
                                profile_newI+=iterator.next()+", ";
                            keyword.setText(profile_newI);

                        }
                        /*if(profile_map.containsKey("user_keyword")){

                            HashMap<String,Boolean> user_keywords=(HashMap)profile_map.get("user_keyword");
                            String profile_userkeyword="";

                            for(String userinterest:user_keywords.keySet()){

                                profile_userkeyword=profile_userkeyword+userinterest+",  ";
                            }
                            keyword.setText(profile_userkeyword);

                        }*/
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

            final StorageReference storeRef = mStorageRef.child("Users").child(currentUserID).child("profile_back.jpg");
            UploadTask uploadTask=storeRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(getActivity(), "Profile Photo Error: " + task.getException().getMessage());
                    }
                    profileback_download_url=storeRef.getDownloadUrl().toString();
                    return storeRef.getDownloadUrl();
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
                .setQuery(db.collection("Feeds").whereEqualTo("uid",currentUserID),Feed.class).build();

        FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position, @NonNull Feed model) {
                        db.collection("Feeds").whereEqualTo("uid",currentUserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                Intent intent = new Intent(getContext(), DeleteFeedActivity.class);
                                                intent.putExtra("id", task.getResult().getDocuments().get(position).getId());
                                                intent.putExtra("feedUri",feed_uri);
                                                startActivity(intent);
                                                return true;
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String userId = getSnapshots().getSnapshot(position).get("uid").toString();
                                String feedId= getSnapshots().getSnapshot(position).getId();
                                Intent profileIntent = new Intent(getContext(), FeedDetailActivity.class);
                                profileIntent.putExtra("userId", userId);
                                profileIntent.putExtra("feedId", feedId);
                                startActivity(profileIntent);
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