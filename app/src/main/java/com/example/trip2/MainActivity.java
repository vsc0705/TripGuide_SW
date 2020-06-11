package com.example.trip2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.trip2.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration mAppBarConfiguration;
    //추가 코드

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    CircleImageView menu_iv;
    TextView username_nav;
    private String currentUserID;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //추가코드
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        currentUserID= mAuth.getCurrentUser().getUid();
        NavigationView navigationView = findViewById(R.id.nav_view);
        final View header = navigationView.getHeaderView(0);
        menu_iv=(CircleImageView)header.findViewById(R.id.menu_iv);
        username_nav = header.findViewById(R.id.user_id);

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
                        DocumentSnapshot document=task.getResult();
                        Map<String, Object> userinfo_map=document.getData();
                        String username = userinfo_map.get("name").toString();
                        username_nav.setText(username);
                    }

                }
            }
        });


        //

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent writeFeed=new Intent(MainActivity.this, FeedWriteActivity.class);
              startActivity(writeFeed);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_set, R.id.nav_list,
                R.id.nav_profile, R.id.nav_point, R.id.nav_request)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra("callRequest")){
            if(getIntent().getExtras().get("callRequest").equals(true)){
                Log.i("MATCH", "매칭요청도착");
                navController.navigate(R.id.nav_request);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if(item.getItemId() == R.id.action_settings){
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.main_logout_option){
            // Custom Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.logout_dailog, null);

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

        return  super.onOptionsItemSelected(item);
    }


    //추가 코드


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void logOutUser() {
        Intent loginIntent =  new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    //


}
