package com.example.trip2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    EditText login_id;
    EditText login_password;
    Button login_login;
    Button login_signin;
    FirebaseAuth login;
    String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id=(EditText)findViewById(R.id.login_id);
        login_password=(EditText)findViewById(R.id.login_password);
        login_login=(Button)findViewById(R.id.login_login);
        login_signin=(Button)findViewById(R.id.login_signin);

        login=login.getInstance();

        login_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in=new Intent(login.this,signin.class);
                startActivity(sign_in);
            }
        });

        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=login_id.getText().toString();
                String password = login_password.getText().toString();
                loginUser(login_id.getText().toString(),login_password.getText().toString());

            }
        });
    }
    private void loginUser(String email, String password){
        login.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"SignInWithEmail:Success");
                    FirebaseUser user=login.getCurrentUser();
                    Intent selection=new Intent(login.this,selection.class);
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
