package com.example.trip2.ui.home;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.Feed;
import com.example.trip2.PicassoTransformations;
import com.example.trip2.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private View view;
    RecyclerView feedList;
    private FirebaseFirestore db;


    private String currentUserID;
    private FirebaseAuth mAuth;

    private String username,user_uri,feed_uri, feed_desc;
    private Timestamp timestamp;

    public HomeFragment(){

    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        view=inflater.inflate(R.layout.fragment_home, container, false);
        feedList=(RecyclerView)view.findViewById(R.id.feed_list);
        feedList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
    public void onStart() {
        super.onStart();
        //query 옵션 추가 자리
        FirestoreRecyclerOptions<Feed> options = new FirestoreRecyclerOptions.Builder<Feed>()
                .setQuery(db.collection("Feeds"), Feed.class).build();


        FirestoreRecyclerAdapter<Feed, FeedViewHolder> feedAdapter=
                new FirestoreRecyclerAdapter<Feed, FeedViewHolder>(options){
                    @Override
                    protected void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position, @NonNull Feed model) {
                        final String user_uid=getSnapshots().getSnapshot(position).get("uid").toString();
                        DocumentReference docRef=getSnapshots().getSnapshot(position).getReference();
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                db.collection("Users").document(user_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            username = task.getResult().get("name").toString();
                                            Log.d(TAG, "UserName: "+username);
                                            if(task.getResult().contains("user_image")){
                                                user_uri=task.getResult().get("user_image").toString();
                                                PicassoTransformations.targetWidth=80;
                                                Picasso.get().load(user_uri)
                                                        .placeholder(R.drawable.default_profile_image)
                                                        .error(R.drawable.default_profile_image)
                                                        .transform(PicassoTransformations.resizeTransformation)
                                                        .into(holder.profileImage);

                                            }
                                            holder.userName.setText(username);
                                        }
                                    }
                                });
                                db.collection("Feeds").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            feed_desc=task.getResult().getDocuments().get(position).get("feed_desc").toString();
                                            timestamp=task.getResult().getDocuments().get(position).getTimestamp("feed_time");
                                            SimpleDateFormat sdf=new SimpleDateFormat("MMM dd EEE", Locale.ENGLISH);
                                            String time=sdf.format(timestamp.toDate());
                                            if(task.getResult().getDocuments().get(position).contains("feed_uri")) {
                                                feed_uri = task.getResult().getDocuments().get(position).get("feed_uri").toString();
                                                PicassoTransformations.targetWidth = 200;
                                                Picasso.get().load(feed_uri)
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.load)
                                                        .transform(PicassoTransformations.resizeTransformation)
                                                        .into(holder.feedImage);
                                                task.getResult().getDocuments().get(position).getReference().collection("LikeMember").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        String likeNum=task.getResult().size()+"";
                                                        holder.tvLikeNum.setText(likeNum);
                                                    }
                                                });
                                                task.getResult().getDocuments().get(position).getReference().collection("LikeMember").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.getResult().exists()){
                                                            holder.btn_like.setLiked(true);
                                                        }
                                                        else {
                                                            holder.btn_like.setLiked(false);
                                                        }
                                                    }
                                                });
                                            }
                                            holder.feedDesc.setText(feed_desc);
                                            holder.userTime.setText(time);
                                        }
                                    }
                                });

                                holder.btn_like.setOnLikeListener(new OnLikeListener() {
                                    @Override
                                    public void liked(LikeButton likeButton) {
                                        String strCurrentNum= holder.tvLikeNum.getText().toString();
                                        int intCurrentNum=Integer.parseInt(strCurrentNum)+1;
                                        holder.tvLikeNum.setText(intCurrentNum+"");
                                        db.collection("Feeds").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    HashMap<String, Object> update_user_data=new HashMap<>();
                                                    update_user_data.put("pushDate", new Timestamp(new Date()));
                                                    task.getResult().getDocuments().get(position).getReference().collection("LikeMember").document(currentUserID).set(update_user_data);

                                                }

                                            }
                                        });
                                    }

                                    @Override
                                    public void unLiked(LikeButton likeButton) {
                                        String strCurrentNum= holder.tvLikeNum.getText().toString();
                                        int intCurrentNum=Integer.parseInt(strCurrentNum)-1;
                                        holder.tvLikeNum.setText(intCurrentNum+"");
                                        db.collection("Feeds").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    task.getResult().getDocuments().get(position).getReference().collection("LikeMember").document(currentUserID).delete();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_feed_layout, viewGroup, false);
                        return new FeedViewHolder(view);
                    }

                };
        feedList.setAdapter(feedAdapter);
        feedAdapter.startListening();
    }
    public static class FeedViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView userName,userTime,feedDesc, tvLikeNum;
        ImageView feedImage;
        LikeButton btn_like;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userTime = itemView.findViewById(R.id.user_time);
            profileImage = itemView.findViewById(R.id.user_profile_image);
            feedImage=itemView.findViewById(R.id.user_feed_image);
            feedDesc = itemView.findViewById(R.id.user_feed_desc);
            btn_like=itemView.findViewById(R.id.btn_like);
            tvLikeNum=itemView.findViewById(R.id.tv_likeNum);
        }
    }

}