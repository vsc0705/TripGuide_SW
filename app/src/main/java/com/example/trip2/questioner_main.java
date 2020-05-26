package com.example.trip2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class questioner_main extends AppCompatActivity {

    private AppBarConfiguration questioner_mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questioner_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.question_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action(Questioner)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.questioner_drawer);
        NavigationView navigationView = findViewById(R.id.questioner_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        questioner_mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.questioner_nav_home, R.id.questioner_nav_set, R.id.questioner_nav_list,
                R.id.questioner_nav_profile, R.id.nav_point)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.questioner_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, questioner_mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickHandler(View view){
        switch (view.getId()){
            case R.id.btn_start: showStartDate();
                break;
            case R.id.btn_end: showEndDate();
                break;
        }
    }

    void showStartDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TextView textView_startdate=(TextView)findViewById(R.id.textView_startdate);
                textView_startdate.setText(year+"."+(month+1)+"."+dayOfMonth);

            }
        },2019, 3, 14);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }
    void showEndDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TextView textView_enddate=(TextView)findViewById(R.id.textView_enddate);
                textView_enddate.setText(year+"."+(month+1)+"."+dayOfMonth);

            }
        },2019, 4, 17);

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();
    }
}
