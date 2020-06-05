package com.example.trip2;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseFirestore db;

    public MessageAdapter(List<Messages> userMessageList){
        this.userMessageList = userMessageList;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, senderTimeText, receiverTimeText, receiverMessageText, senderImageTimeText, receiverImageTimeText;
        RoundedImageView senderImageMsg, receiverImageMsg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = (TextView)itemView.findViewById(R.id.text_message_send);
            senderTimeText = (TextView)itemView.findViewById(R.id.text_message_time_send);
            receiverMessageText = (TextView)itemView.findViewById(R.id.text_message_receiver);
            receiverTimeText =(TextView)itemView.findViewById(R.id.text_message_time_receiver);
            senderImageMsg = itemView.findViewById(R.id.messageImageVsender);
            receiverImageMsg = itemView.findViewById(R.id.messageImageVreceiver);
            senderImageTimeText = (TextView)itemView.findViewById(R.id.image_message_time_send);
            receiverImageTimeText = (TextView)itemView.findViewById(R.id.image_message_time_receiver);
        }
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_message_personal, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        final Messages messages = userMessageList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        if(fromMessageType.equals("text")){
            messageViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderTimeText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverTimeText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderImageMsg.setVisibility(View.GONE);
            messageViewHolder.receiverImageMsg.setVisibility(View.GONE);
            messageViewHolder.senderImageTimeText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverImageTimeText.setVisibility(View.INVISIBLE);

            if(fromUserID.equals(messageSenderID)){
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage());
                messageViewHolder.senderTimeText.setVisibility(View.VISIBLE);
                messageViewHolder.senderTimeText.setText(messages.getTime());
            }
            else{
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage());
                messageViewHolder.receiverTimeText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverTimeText.setText(messages.getTime());
            }
        }
        if(fromMessageType.equals("image")){
            messageViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessageText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderTimeText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverTimeText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderImageMsg.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverImageMsg.setVisibility(View.INVISIBLE);
            messageViewHolder.senderImageTimeText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverImageTimeText.setVisibility(View.INVISIBLE);


            if(fromUserID.equals(messageSenderID)){
                messageViewHolder.senderImageMsg.setVisibility(View.VISIBLE);
                messageViewHolder.senderImageTimeText.setVisibility(View.VISIBLE);
                messageViewHolder.senderImageTimeText.setText(messages.getTime());
                Picasso.get()
                        .load(messages.getMessage())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.error)
                        .into(messageViewHolder.senderImageMsg);
                messageViewHolder.senderImageMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), fullScreenImageViewer.class);
                        intent.putExtra("uri", messages.getMessage());
                        v.getContext().startActivity(intent);
                    }
                });
            } else{
                messageViewHolder.receiverImageMsg.setVisibility(View.VISIBLE);
                messageViewHolder.receiverImageTimeText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverImageTimeText.setText(messages.getTime());
                Picasso.get()
                        .load(messages.getMessage())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.error)
                        .into(messageViewHolder.receiverImageMsg);
                messageViewHolder.receiverImageMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), fullScreenImageViewer.class);
                        intent.putExtra("uri", messages.getMessage());
                        v.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
    @Override
    public int getItemCount() {
        return userMessageList.size();
    }
}
