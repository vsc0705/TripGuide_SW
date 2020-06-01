package com.example.trip2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip2.ui.set.SecondActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {

    private View requestsFragmentView;
    private RecyclerView mRequestsList;
    private DatabaseReference chatRequestsRef, userRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter fsAdapter;
    private String reqname,reqstatus, user_uri;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        chatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        requestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestsList = (RecyclerView)requestsFragmentView.findViewById(R.id.chat_requests_list);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return requestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

//        FirebaseRecyclerOptions<Contacts> options =
//                new FirebaseRecyclerOptions.Builder<Contacts>()
//                        .setQuery(chatRequestsRef.child(currentUserId), Contacts.class)
//                        .build();

        FirestoreRecyclerOptions<Contacts> fsOptions =
                new FirestoreRecyclerOptions.Builder<Contacts>().setQuery(db.collection("Users").document(currentUserId).collection("Matching").whereEqualTo("ismatched", false), Contacts.class).build();

        fsAdapter = new FirestoreRecyclerAdapter<Contacts, RequestsViewHolder>(fsOptions){

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, final int position, @NonNull final Contacts model) {
                holder.itemView.findViewById(R.id.requests_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.requests_cancel_btn).setVisibility(View.VISIBLE);

                // listuserid는 나의 매칭 항목에 있는 상대방의 uid 목록
               final String listUserId = getSnapshots().getSnapshot(position).getId();
                DocumentReference docRef = getSnapshots().getSnapshot(position).getReference();
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> reqMap = documentSnapshot.getData();
                            //친구 요청을 받은 경우
                            if(reqMap.containsKey("received")){
                                db.collection("Users").document(listUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                           reqname = task.getResult().get("name").toString();
                                           reqstatus=task.getResult().get("status").toString();
                                            if(task.getResult().contains("user_image")){
                                                user_uri=task.getResult().get("user_image").toString();
                                                PicassoTransformations.targetWidth=70;
                                                Picasso.get().load(user_uri)
                                                        .placeholder(R.drawable.default_profile_image)
                                                        .error(R.drawable.default_profile_image)
                                                        .transform(PicassoTransformations.resizeTransformation)
                                                        .into(holder.profileImage);
                                            }
                                           holder.userName.setText(reqname);
                                           holder.userStatus.setText(reqstatus);
                                        }
                                    }
                                });
                                //수락한 경우
                                holder.itemView.findViewById(R.id.requests_accept_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Map<String, Object> ismatched = new HashMap<>();
                                        ismatched.put("ismatched", true);
                                        //수락한 경우에 일단 내 매칭리스트에서 ismatched 값을 넣음
                                        getSnapshots().getSnapshot(position).getReference().set(ismatched).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //내 매칭리스트에 ismatched가 들어간 다음에는 상대의 매칭리스트에도 ismatched 값을 넣어줌
                                                db.collection("Users").document(listUserId).collection("Matching").document(currentUserId).set(ismatched).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        //상대 매칭리스트에 ismatched값이 들어가면 상대 매칭리스트에서 sent 값을 없애줌
                                                        Map<String,Object> removesent = new HashMap<>();
                                                        removesent.put("sent", FieldValue.delete());
                                                        db.collection("Users").document(listUserId).collection("Matching").document(currentUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //상대 매칭 리스트에서 sent 값이 없어지면, 내 매칭 리스트에서 recieved 값도 없애줌
                                                                Map<String,Object> removereceived = new HashMap<>();
                                                                removereceived.put("received", FieldValue.delete());
                                                                db.collection("Users").document(currentUserId).collection("Matching").document(listUserId).update(removereceived).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(getContext(), "매칭이 수락되었습니다",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                                //거절한 경우
                                holder.itemView.findViewById(R.id.requests_cancel_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //상대 매칭리스트에서 sent 값을 없애줌
                                        Map<String,Object> removesent = new HashMap<>();
                                        removesent.put("sent", FieldValue.delete());
                                        db.collection("Users").document(listUserId).collection("Matching").document(currentUserId).update(removesent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //상대 매칭 리스트에서 sent 값이 없어지면, 내 매칭 리스트에서 recieved 값도 없애줌
                                                Map<String,Object> removereceived = new HashMap<>();
                                                removereceived.put("received", FieldValue.delete());
                                                db.collection("Users").document(currentUserId).collection("Matching").document(listUserId).update(removereceived).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getContext(), "매칭이 거절되었습니다",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                            //친구 요청을 보낸 경우
                            if(reqMap.containsKey("sent")){

                            }
                        }
                    }
                });


            }
        };



//        final FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {
//                        holder.itemView.findViewById(R.id.requests_accept_btn).setVisibility(View.VISIBLE);
//                        holder.itemView.findViewById(R.id.requests_cancel_btn).setVisibility(View.VISIBLE);
//
//                        final String listUserId = getRef(position).getKey();
//                        DatabaseReference getTypeRef = getRef(position).child("requestType").getRef();
//                        getTypeRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.exists()){
//                                    String type = dataSnapshot.getValue().toString();
//                                    if(type.equals("received")){
//                                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();
//                                                holder.userName.setText(requestUserName);
//
//                                                //test onClick Button accept or decline
//                                                holder.itemView.findViewById(R.id.requests_accept_btn)
//                                                        .setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View v) {
//                                                                contactsRef.child(currentUserId).child(listUserId).child("Contact")
//                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                        if(task.isSuccessful()){
//                                                                            contactsRef.child(listUserId).child(currentUserId).child("Contact")
//                                                                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                @Override
//                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                    if(task.isSuccessful()){
//                                                                                        chatRequestsRef.child(currentUserId).child(listUserId)
//                                                                                                .removeValue()
//                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                    @Override
//                                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                                        if(task.isSuccessful()){
//                                                                                                            chatRequestsRef.child(listUserId).child(currentUserId)
//                                                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                @Override
//                                                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                                                    Toast.makeText(getContext(), "New Contact Saved",Toast.LENGTH_SHORT).show();
//                                                                                                                }
//                                                                                                            });
//                                                                                                        }
//                                                                                                    }
//                                                                                                });
//                                                                                    }
//                                                                                }
//                                                                            });
//
//                                                                        }
//                                                                    }
//                                                                });
//                                                            }
//                                                        });
//
//                                                holder.itemView.findViewById(R.id.requests_cancel_btn)
//                                                        .setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        chatRequestsRef.child(currentUserId).child(listUserId)
//                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if(task.isSuccessful()){
//                                                                    chatRequestsRef.child(listUserId).child(currentUserId)
//                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if(task.isSuccessful()){
//                                                                                Toast.makeText(getContext(), "Contact Deleted",Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//                                                });
//
//
//                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        CharSequence options[] = new CharSequence[]{
//                                                                "Accept",
//                                                                "Cancel"
//                                                        };
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                                        builder.setTitle(requestUserName + " Chat Request");
//                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                if(which == 0){
//                                                                    contactsRef.child(currentUserId).child(listUserId).child("Contact")
//                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if(task.isSuccessful()){
//                                                                                contactsRef.child(listUserId).child(currentUserId).child("Contact")
//                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        if(task.isSuccessful()){
//                                                                                            chatRequestsRef.child(currentUserId).child(listUserId)
//                                                                                                    .removeValue()
//                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                        @Override
//                                                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                                                            if(task.isSuccessful()){
//                                                                                                                chatRequestsRef.child(listUserId).child(currentUserId)
//                                                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                    @Override
//                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                                                        Toast.makeText(getContext(), "New Contact Saved",Toast.LENGTH_SHORT).show();
//                                                                                                                    }
//                                                                                                                });
//                                                                                                            }
//                                                                                                        }
//                                                                                                    });
//                                                                                        }
//                                                                                    }
//                                                                                });
//
//                                                                            }
//                                                                        }
//                                                                    });
//
//                                                                }
//                                                                else if(which == 1){
//                                                                    chatRequestsRef.child(currentUserId).child(listUserId)
//                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if(task.isSuccessful()){
//                                                                                chatRequestsRef.child(listUserId).child(currentUserId)
//                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        if(task.isSuccessful()){
//                                                                                            Toast.makeText(getContext(), "Contact Deleted",Toast.LENGTH_SHORT).show();
//                                                                                        }
//                                                                                    }
//                                                                                });
//                                                                            }
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                        });
//                                                        builder.show();
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                    }
//                                    else if(type.equals("sent")){
//                                        Button requestSendBtn = holder.itemView.findViewById(R.id.requests_accept_btn);
//                                        requestSendBtn.setText(R.string.req_sent);
//                                        holder.itemView.findViewById(R.id.requests_cancel_btn).setVisibility(View.INVISIBLE);
//                                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();
//                                                holder.userName.setText(requestUserName);
//                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        CharSequence options[] = new CharSequence[]{
//                                                                "Cancel Chat Request"
//                                                        };
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                                        builder.setTitle("Already sent request");
//                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//                                                                if(which == 0){
//                                                                    chatRequestsRef.child(currentUserId).child(listUserId)
//                                                                            .removeValue()
//                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                @Override
//                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                    if(task.isSuccessful()){
//                                                                                        chatRequestsRef.child(listUserId)
//                                                                                                .child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                            @Override
//                                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                                if(task.isSuccessful()){
//                                                                                                    Toast.makeText(getContext(), "You have cancelled  the chat request",Toast.LENGTH_SHORT).show();
//                                                                                                }
//                                                                                            }
//                                                                                        });
//                                                                                    }
//
//                                                                                }
//                                                                            });
//                                                                }
//                                                            }
//                                                        });
//                                                        builder.show();
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                    @NonNull
//                    @Override
//                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
//                        RequestsViewHolder holder = new RequestsViewHolder(view);
//                        return holder;
//                    }
//                };
        mRequestsList.setAdapter(fsAdapter);
        fsAdapter.startListening();
    }
    public static class RequestsViewHolder extends  RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button acceptButton, cancelButton;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            acceptButton = (Button)itemView.findViewById(R.id.requests_accept_btn);
            cancelButton = (Button)itemView.findViewById(R.id.requests_cancel_btn);

        }
    }
}
