package com.example.trip2.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.ChatActivity;
import com.example.trip2.Contacts;
import com.example.trip2.EvaluationActivity;
import com.example.trip2.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hasnat.sweettoast.SweetToast;


public class questioner_list extends Fragment {
    private static final String TAG = "questioner_list";
    private View privateChatsView;
    private RecyclerView chatsList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    String userstatus,user_uri;
    FirestoreRecyclerOptions<Contacts> options;
    FirestoreRecyclerAdapter<Contacts, ChatsViewHolder> fsAdapter;

    public questioner_list(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        privateChatsView =  inflater.inflate(R.layout.fragment_list, container, false);

        chatsList = (RecyclerView)privateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatsView;
    }
    @Override
    public void onStart() {
        super.onStart();
        options = new FirestoreRecyclerOptions.Builder<Contacts>()
                .setQuery(db.collection("Users").document(currentUserId).collection("Matching").whereEqualTo("ismatched", true), Contacts.class).build();

        fsAdapter =
                new FirestoreRecyclerAdapter<Contacts, ChatsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String user_uid = getSnapshots().getSnapshot(position).getId();
                        DocumentReference docRef = getSnapshots().getSnapshot(position).getReference();
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                db.collection("Users").document(user_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            final String username = task.getResult().get("name").toString();
                                            userstatus = task.getResult().get("status").toString();
                                            if(task.getResult().contains("user_image")){
                                                user_uri=task.getResult().get("user_image").toString();
                                                Picasso.get().load(user_uri)
                                                        .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                                                        .placeholder(R.drawable.default_profile_image)
                                                        .error(R.drawable.default_profile_image)
                                                        .resize(0,90)
                                                        .into(holder.profileImage, new Callback() {
                                                            @Override
                                                            public void onSuccess() {

                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                Picasso.get().load(user_uri)
                                                                        .placeholder(R.drawable.default_profile_image)
                                                                        .error(R.drawable.default_profile_image)
                                                                        .resize(0,90)
                                                                        .into(holder.profileImage);

                                                            }
                                                        });
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
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                    chatIntent.putExtra("visitUserId", user_uid);
                                                    chatIntent.putExtra("visitUserName", username);
                                                    //chatIntent.putExtra("visitUserImage", localFile.getAbsolutePath());
                                                    startActivity(chatIntent);
                                                }
                                            });
                                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    setvUid(user_uid);
                                                    return false;
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                        registerForContextMenu(view);
                        return new ChatsViewHolder(view);
                    }
                };
        chatsList.setAdapter(fsAdapter);
        fsAdapter.startListening();
    }

    private String vUid;
    public String getvUid(){
        return vUid;
    }
    public void setvUid(String vUid){
        this.vUid = vUid;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mInflater = getActivity().getMenuInflater();
        mInflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.listmenu_report:
                SweetToast.info(this.getContext(), "신고하기\n"+getvUid());
                return true;
            case R.id.listmenu_endmatch:
                SweetToast.info(this.getContext(), "매칭종료 및 평가");
                Intent evalIntent = new Intent(getContext(), EvaluationActivity.class);
                evalIntent.putExtra("rateeUid", getvUid());
                evalIntent.putExtra("raterUid", currentUserId);
                startActivity(evalIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
