package com.example.trip2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class questioner_main extends AppCompatActivity implements View.OnClickListener{

    private AppBarConfiguration questioner_mAppBarConfiguration;
    //추가 코드
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawer;
    private NavController navController;
    CircleImageView menu_iv;
    TextView username_nav;
    private String currentUserID;
    FloatingActionButton q_fab;
    FloatingActionButton q_fab1;
    FloatingActionButton q_fab2;
    //
    Animation fab_open,fab_close;
    private Boolean isFabOpen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questioner_main);

        fab_open= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

     q_fab = findViewById(R.id.q_fab);
        q_fab1 = findViewById(R.id.q_fab_1);
        q_fab2 = findViewById(R.id.q_fab_2);

        q_fab.setOnClickListener(this);
        q_fab1.setOnClickListener(this);
        q_fab2.setOnClickListener(this);




        //추가코드
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        drawer = (DrawerLayout) findViewById(R.id.questioner_drawer);
        currentUserID= mAuth.getCurrentUser().getUid();
        NavigationView navigationView = findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        menu_iv=(CircleImageView)header.findViewById(R.id.menu_iv);
        username_nav = header.findViewById(R.id.user_id);
        //

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.questioner_drawer);

        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().contains("user_image")){
                        final String userUri=task.getResult().get("user_image").toString();
                        Picasso.get().load(userUri)
                                .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .resize(0,170)
                                .into(menu_iv, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(userUri)
                                                .placeholder(R.drawable.default_profile_image)
                                                .error(R.drawable.default_profile_image)
                                                .resize(0,170)
                                                .into(menu_iv);

                                    }
                                });
                    }
                    String username = task.getResult().get("name").toString();
                    username_nav.setText(username);
                }
            }
        });


        //유저이름가져오기



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        questioner_mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.questioner_nav_home, R.id.questioner_nav_set, R.id.questioner_nav_list,
                R.id.questioner_nav_profile,R.id.questioner_nav_wishlist, R.id.nav_evaluation)
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
    public void goGuide(View v){
        Intent guide=new Intent(questioner_main.this, questioner_GuideActivity.class);
        startActivity(guide);
    }

    //
    //메뉴 열려 있을 경우 뒤로 가기 키 누르면 메뉴가 닫히도록 설정
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.questioner_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.q_fab :
                anim();
                break;
            case R.id.q_fab_1 :
                anim();
                Toast.makeText(this,"프로필",Toast.LENGTH_SHORT).show();
                break;
            case R.id.q_fab_2 :
                anim();
                Toast.makeText(this, "어디든간다", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void anim(){
        if (isFabOpen) {
            q_fab1.startAnimation(fab_close);
            q_fab2.startAnimation(fab_close);
            q_fab1.setClickable(false);
            q_fab2.setClickable(false);
            isFabOpen = false;
        } else {
            q_fab1.startAnimation(fab_open);
            q_fab2.startAnimation(fab_open);
            q_fab1.setClickable(true);
            q_fab2.setClickable(true);
            isFabOpen = true;
        }
    }

}
