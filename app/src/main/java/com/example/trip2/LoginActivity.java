package com.example.trip2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText et_id, et_password;
    Button btn_login, btn_signup;
    private FirebaseAuth mLogin;
    ProgressBar progressBar;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogin=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        et_id=(EditText)findViewById(R.id.etId);
        et_password=(EditText)findViewById(R.id.etPassword);
        btn_login=(Button)findViewById(R.id.btnLogin);
        btn_signup=(Button)findViewById(R.id.btnRegister);
        progressBar=(ProgressBar) findViewById(R.id.progressbar);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(sign_up);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail=et_id.getText().toString();
                String stPassword = et_password.getText().toString();

                if(stEmail.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Please Insert Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if(stPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Please Insert Password", Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                loginUser(et_id.getText().toString(),et_password.getText().toString());

            }
        });
    }
    private void loginUser(String email, String password){
        mLogin.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Log.d(TAG,"SignInWithEmail:Success");
                            FirebaseUser user= mLogin.getCurrentUser();

                            //여기서부터 아래까지 이메일 값 전달 용도 db연결되면 db 값으로 대체
                            String stUserEmail=user.getEmail();//이메일
                            String stUserName=user.getDisplayName();//이름
                            Log.d(TAG, "stUserEmail: "+stUserEmail+", stUserName: "+stUserName);

                            SharedPreferences sharedPref = getSharedPreferences("shared", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("email",stUserEmail);
                            editor.commit();
                            //

                            Intent selection=new Intent(LoginActivity.this, SelectionActivity.class);
                            startActivity(selection);
                        }
                        else{
                            Log.w(TAG,"signInWithEmail:failure",task.getException());
                            Toast.makeText(getApplicationContext(),"Authentication failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
