package com.example.trip2;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().get("pushType").equals("message")){
            if(getTopActivity().equals("ChatActivity") && remoteMessage.getData().get("chatRoomId").equals(((ChatActivity)ChatActivity.context_chat).chatroomId)){
                Log.i("MESSAGERECEIVED", "Do not show notification");
            } else{
                sendMessageNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData().get("chatRoomId"), remoteMessage.getData().get("visitUserId"), remoteMessage.getData().get("visitUserName"));

            }
        } else if(remoteMessage.getData().get("pushType").equals("request")){
            Log.i("MESSAGERECEIVED", "request Push");
            sendRequestNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else if(remoteMessage.getData().get("pushType").equals("isaccept")){
            Log.i("MESSAGERECEIVED", "match result Push");
            sendMatchResultNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else if(remoteMessage.getData().get("pushType").equals("matchEnd")){
            Log.i("MESSAGERECEIVED", "Match End Notification");
            sendMatchEndNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

    }

    private void sendRequestNotification(String title, String body)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("callRequest", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        String chId = "request";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 알림 왔을때 사운드.
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, chId)
                .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_launcher_foreground: R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* 안드로이드 오레오 버전 대응 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String chName = "매칭 요청 알림";
            NotificationChannel channel = new NotificationChannel(chId, chName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, notiBuilder.build());

    }

    private void sendMatchResultNotification(String title, String body)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        String chId = "result";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 알림 왔을때 사운드.
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, chId)
                .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_launcher_foreground: R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* 안드로이드 오레오 버전 대응 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String chName = "매칭 결과 알림";
            NotificationChannel channel = new NotificationChannel(chId, chName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, notiBuilder.build());

    }

    private void sendMessageNotification(String title, String body, String chatRoomId, String visitUserId, String visitUserName)
    {
        //현재 백그라운드에서 푸시메시지 수신한 경우 로그인 액티비티가 먼저 뜨는 이슈 있음
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("visitUserId", visitUserId);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("visitUserName", visitUserName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String chId = "message";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 알림 왔을때 사운드.
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, chId)
                .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_launcher_foreground: R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* 안드로이드 오레오 버전 대응 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String chName = "메시지 알림";
            NotificationChannel channel = new NotificationChannel(chId, chName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, notiBuilder.build());

    }

    private void sendMatchEndNotification(String title, String body)
    {
        String chId = "matchend";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 알림 왔을때 사운드.
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, chId)
                .setSmallIcon(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_launcher_foreground: R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* 안드로이드 오레오 버전 대응 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String chName = "매칭 종료 알림";
            NotificationChannel channel = new NotificationChannel(chId, chName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, notiBuilder.build());

    }

    private String getTopActivity(){
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        ComponentName componentName= info.get(0).topActivity;
        return componentName.getShortClassName().substring(1);
    }
}

