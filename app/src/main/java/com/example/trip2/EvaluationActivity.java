package com.example.trip2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import xyz.hasnat.sweettoast.SweetToast;

public class EvaluationActivity extends AppCompatActivity {

    RatingBar accuracyStar, speedStar, kindStar;
    EditText opinion;
    Button submit;
    String raterId, rateeId;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        raterId = getIntent().getExtras().get("raterUid").toString();
        rateeId = getIntent().getExtras().get("rateeUid").toString();

        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_evaluation);
        accuracyStar=(RatingBar)findViewById(R.id.evaluation_accuracyStar);
        speedStar=(RatingBar)findViewById(R.id.evaluation_speedStar);
        kindStar=(RatingBar)findViewById(R.id.evaluation_kindStar);

        opinion=(EditText)findViewById(R.id.evaluation_opinion);
        submit = (Button)findViewById(R.id.evaluation_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SweetToast.info(v.getContext(), String.valueOf(accuracyStar.getRating())+"\n"+String.valueOf(speedStar.getRating())+"\n"+String.valueOf(kindStar.getRating()));
                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(v.getContext(), android.R.style.Theme_DeviceDefault_Dialog);
                confirmDialog.setMessage("정말 매칭을 종료하시겠습니까?\n").setTitle("매칭 종료").setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        evalUser();
                        SweetToast.info(getApplicationContext(), "매칭 종료");
                        endMatch();
                        finish();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SweetToast.info(getApplicationContext(), "평가 취소");
                    }
                }).setCancelable(false).show();

            }
        });

//        getIntent().getExtras().get("rateeUid").toString();
//        SweetToast.info(this, "평가자: "+getIntent().getExtras().get("raterUid").toString());
    }

    private void evalUser(){
        DocumentReference evalDoc = db.collection("Evaluation").document();
        String evalDocId = evalDoc.getId();

        HashMap evalInfo = new HashMap();
        evalInfo.put("rater", raterId);
        evalInfo.put("ratee", rateeId);
        evalInfo.put("accuracy", accuracyStar.getRating());
        evalInfo.put("speed", speedStar.getRating());
        evalInfo.put("kindness", kindStar.getRating());
        evalInfo.put("opinion", opinion.getText().toString());
        db.collection("Evaluation").document(evalDocId).set(evalInfo);
    }

    private void endMatch(){
        //채팅 내역도 삭제 필요?
        db.collection("Users").document(raterId).collection("Matching").document(rateeId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                db.collection("Users").document(rateeId).collection("Matching").document(raterId).delete();
                sendFCM(rateeId, raterId);
            }
        });
    }

    private void sendFCM(final String receiverId, String senderId){
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAAst3LJCQ:APA91bFXxaAjnupdToP6oUYp8qXK8akknY5EKOo-8_ZXURJ64zraxbV27OnKrMhIaQm9hKx4JcPqtQRvl1_O6xbob-xv66WEvXFrV7wzLAXHsJA_tt1RTXXLP7v-9fXq6BXQsliEPFT4";
        db.collection("Users").document(senderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    pushMsg.put("body", senderName+"님과의 매칭이 종료되었습니다.");
                                    pushMsg.put("title", "매칭 종료");
                                    pushData.put("pushType", "matchEnd");
                                    pushRoot.put("notification", pushMsg);
                                    pushRoot.put("to", receiverFCMId);
                                    pushRoot.put("data", pushData);
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
}