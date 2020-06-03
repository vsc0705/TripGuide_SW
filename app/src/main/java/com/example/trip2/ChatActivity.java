package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar chatToolBar;
    private String messageReceiverID, messageReceiverName,messageSenderID;
    private TextView userName;

    private ImageButton sendMessageBtn;
    private EditText messageInputText;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseFirestore db;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private DocumentReference chatroomRef;
    private DocumentReference messagebody;
    private String chatroomId;
    private String messagebodyid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();


        messageReceiverID = getIntent().getExtras().get("visitUserId").toString();
        messageReceiverName = getIntent().getExtras().get("visitUserName").toString();
        Log.i("TEST", "메시지 수신자 ID: "+messageReceiverID);
        Log.i("TEST", "메시지 수신자 이름: "+messageReceiverName);
//        Toast.makeText(ChatActivity.this, messageReceiverID, Toast.LENGTH_LONG).show();
//        Toast.makeText(ChatActivity.this, messageReceiverName, Toast.LENGTH_LONG).show();

        InitializeControllers();

        userName.setText(messageReceiverName);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void SendMessage() {
        String messageText = messageInputText.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "first write your message...", Toast.LENGTH_LONG).show();
        }
        else {
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
//            messageTextBody.put("time", new Timestamp(new Date()));
            // 채팅 순서가 뒤섞이는 일을 방지하기 위해 서버 타임스탬프를 사용하는 것이 좋을 것으로 보이나
            // 이를 구현하는데에 약간 버그가 있어 현재 기기 시간으로 타임스탬프 사용
            messageTextBody.put("time", FieldValue.serverTimestamp());

            messagebody = db.collection("ChatRooms").document(chatroomId).collection("Messages").document();
            messagebodyid = messagebody.getId();
            messagebody.set(messageTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });
//
//
//            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
//            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;
//
//            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
//                    .child(messageSenderID)
//                    .child(messageReceiverID)
//                    .push();
//
//            String messagePushID = userMessageKeyRef.getKey();
//
//            Map messageTextBody = new HashMap();
//            messageTextBody.put("message", messageText);
//            messageTextBody.put("type", "text");
//            messageTextBody.put("from", messageSenderID);
//            messageTextBody.put("time", currentTime);
//
//            Map messageBodyDetails = new HashMap();
//            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                    }
//                    messageInputText.setText("");
//                }
//            });

        }

    }
    private void InitializeControllers() {
        chatToolBar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(0);
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);

        actionBar.setCustomView(actionBarView);

        userName = (TextView)findViewById(R.id.custom_profile_name);

        sendMessageBtn = (ImageButton)findViewById(R.id.send_message_btn);
        messageInputText = (EditText)findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView)findViewById(R.id.private_message_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();

        db.collection("ChatRooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String existroomid = null;
                for(DocumentSnapshot res:task.getResult().getDocuments()){
                    ArrayList uc = (ArrayList) res.getData().get("Users");
                    if(uc.contains(messageReceiverID) && uc.contains(messageSenderID)) {
                        existroomid = res.getId();
                        chatroomId = existroomid;
                    }
                }
                if(existroomid==null){
                    Map chatUsers = new HashMap();
                    chatUsers.put("Users", Arrays.asList(messageSenderID, messageReceiverID));
                    chatroomRef = db.collection("ChatRooms").document();
                    chatroomId = chatroomRef.getId();
                    chatroomRef.set(chatUsers);
                }

                db.collection("ChatRooms").document(chatroomId).collection("Messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentChange dc:queryDocumentSnapshots.getDocumentChanges()){
                            Messages messages = dc.getDocument().toObject(Messages.class, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
                            switch (dc.getType()){
                                case ADDED:
                                    messagesList.add(messages);
                                    messageAdapter.notifyDataSetChanged();
                                    userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                    break;
                                case MODIFIED:
                                    // 여기때문에 채팅 증발현상 생김
                                    messagesList.remove(messagesList.size()-1);
                                    messagesList.add(messages);
                                    messageAdapter.notifyDataSetChanged();
                                    userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                                    break;
                            }
                        }

                    }
                });

            }
        });



//        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        Messages messages = dataSnapshot.getValue(Messages.class);
//                        messagesList.add(messages);
//                        messageAdapter.notifyDataSetChanged();
//                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
    }
}
