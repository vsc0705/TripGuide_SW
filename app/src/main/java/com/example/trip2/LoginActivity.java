package com.example.trip2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText et_id, et_password;
    Button btn_login, btn_signup;
    private FirebaseAuth mLogin;

    //추가코드
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private ProgressDialog loadingBar;
    //
    //ProgressBar progressBar;
    FirebaseDatabase database;

    //로그인 정보 저장 코드 2020.05.29 HSY
    private String saved_id;
    private String saved_pwd;
    private boolean saved_LoginData;
    private CheckBox checkBox;
    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //로그인 정보 저장 코드 2020.05.29 HSY
        //설정값 불러오기
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        mLogin=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        //추가 코드
        loadingBar = new ProgressDialog(this);
        currentUser = mLogin.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        //

        et_id=(EditText)findViewById(R.id.etId);
        et_password=(EditText)findViewById(R.id.etPassword);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        btn_login=(Button)findViewById(R.id.btnLogin);
        btn_signup=(Button)findViewById(R.id.btnRegister);
        //progressBar=(ProgressBar) findViewById(R.id.progressbar);
        if(saved_LoginData){
            et_id.setText(saved_id);
            et_password.setText(saved_pwd);
            checkBox.setChecked(saved_LoginData);
        }

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail=et_id.getText().toString();
                String stPassword = et_password.getText().toString();

                if(stEmail.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Please Insert Email", Toast.LENGTH_LONG).show();
                }
                else if(stPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Please Insert Password", Toast.LENGTH_LONG).show();
                }
                else{
                    loadingBar.setTitle(R.string.logging_in);
                    loadingBar.setMessage("Please wait....");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

                    loginUser(et_id.getText().toString(),et_password.getText().toString());
                }
            }
        });
    }
    // 추가코드


    private void loginUser(String email, String password){
        mLogin.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Log.d(TAG,"SignInWithEmail:Success");
                            FirebaseUser user= mLogin.getCurrentUser();
                            String currentUserId = mLogin.getCurrentUser().getUid();
                            String deviceToken = String.valueOf(FirebaseInstanceId.getInstance().getInstanceId());

                            userRef.child(currentUserId).child("deviceToken")
                                    .setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                SendUserToMainActivity();
                                                Toast.makeText(LoginActivity.this, "Logged", Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                            }

                                        }
                                    });
                            save();
                            SendUserToMainActivity();
                            Toast.makeText(LoginActivity.this,
                                    "Logged in Successfull...", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();



                            //여기서부터 아래까지 이메일 값 전달 용도 db연결되면 db 값으로 대체
                            String stUserEmail=user.getEmail();//이메일
                            String stUserName=user.getDisplayName();//이름
                            Log.d(TAG, "stUserEmail: "+stUserEmail+", stUserName: "+stUserName);

                            SharedPreferences sharedPref = getSharedPreferences("shared", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("email",stUserEmail);
                            editor.commit();
                            //

                        }
                        else{
                            Log.w(TAG,"signInWithEmail:failure",task.getException());
                            Toast.makeText(getApplicationContext(),"Authentication failed",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, SelectionActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(registerIntent);
    }

    //로그인 정보 저장 코드 2020.05.29 HSY
    private void load(){
        //기본값, 저장된 정보 없을경우
        saved_LoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        saved_id = appData.getString("ID", "");
        saved_pwd = appData.getString("PWD", "");
    }

    private void save(){
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", et_id.getText().toString().trim());
        editor.putString("PWD", et_password.getText().toString().trim());
        editor.apply();
    }
}
