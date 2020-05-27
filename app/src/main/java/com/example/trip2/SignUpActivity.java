package com.example.trip2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trip2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    EditText et_name;
    EditText et_email;
    EditText et_passowrd;
    Button btn_signup;
    Button btn_cancel;
    private FirebaseAuth mAuth;
    String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth=FirebaseAuth.getInstance();

        et_name=(EditText)findViewById(R.id.signin_name);
        et_email=(EditText)findViewById(R.id.signin_email);
        et_passowrd=(EditText)findViewById(R.id.signin_password);
        btn_signup=(Button)findViewById(R.id.signin_signin);
        btn_cancel=(Button)findViewById(R.id.signin_cnacel);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(et_email.getText().toString(),et_passowrd.getText().toString());
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){
                            Toast.makeText(SignUpActivity.this,"가입성공",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"cretaeUserWithEmail:success");
                            FirebaseUser user=mAuth.getCurrentUser();

                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "가입실패", Toast.LENGTH_SHORT).show();
                            Log.w(TAG,"createUserWithEmail:failure",task.getException());
                        }
                    }
                });
    }
}
