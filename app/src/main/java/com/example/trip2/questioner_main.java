package com.example.trip2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class questioner_main extends AppCompatActivity {

    private AppBarConfiguration questioner_mAppBarConfiguration;
    //추가 코드
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;
    private NavController navController;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questioner_main);


        //추가코드
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        drawer = (DrawerLayout) findViewById(R.id.questioner_drawer);
        //

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.questioner_drawer);
        NavigationView navigationView = findViewById(R.id.questioner_nav_view);

        //유저이름가져오기
        View header = navigationView.getHeaderView(0);
        final TextView username_nav = header.findViewById(R.id.user_id);

        String userid = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    Map<String, Object> userinfo_map=document.getData();
                    String username = userinfo_map.get("name").toString();
                    username_nav.setText(username);
                }
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        questioner_mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.questioner_nav_home, R.id.questioner_nav_set, R.id.questioner_nav_list,
                R.id.questioner_nav_profile)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.questioner_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, questioner_mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra("matchResult")){
            if(getIntent().getExtras().get("matchResult").equals(true)){
                navController.navigate(R.id.questioner_nav_list);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.questioner_nav_host_fragment);
        return NavigationUI.navigateUp(navController, questioner_mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.getItemId() == R.id.action_settings){
            Intent intent=new Intent(questioner_main.this,SettingsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.main_logout_option){
            // Custom Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(questioner_main.this);
            View view = LayoutInflater.from(questioner_main.this).inflate(R.layout.logout_dailog, null);

            ImageButton imageButton = view.findViewById(R.id.logoutImg);
            imageButton.setImageResource(R.drawable.logout);
            builder.setCancelable(true);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton("YES, Log out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                    logOutUser();
                }
            });
            builder.setView(view);
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    //추가 코드


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(questioner_main.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void logOutUser() {
        Intent loginIntent =  new Intent(questioner_main.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //
}
