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

public class signin extends AppCompatActivity {
    EditText signin_name;
    EditText signin_email;
    EditText signin_passowrd;
    Button signin_button;
    Button signin_cancel;
    private FirebaseAuth mAuth;
    String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth=FirebaseAuth.getInstance();

        signin_name=(EditText)findViewById(R.id.signin_name);
        signin_email=(EditText)findViewById(R.id.signin_email);
        signin_passowrd=(EditText)findViewById(R.id.signin_password);
        signin_button=(Button)findViewById(R.id.signin_signin);
        signin_cancel=(Button)findViewById(R.id.signin_cnacel);

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(signin_email.getText().toString(),signin_passowrd.getText().toString());
                finish();
            }
        });
        signin_cancel.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(getApplicationContext(),"가입성공",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"cretaeUserWithEmail:success");
                            FirebaseUser user=mAuth.getCurrentUser();

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "가입실패", Toast.LENGTH_SHORT).show();
                            Log.w(TAG,"createUserWithEmail:failure",task.getException());
                        }
                    }
                });
    }
}
