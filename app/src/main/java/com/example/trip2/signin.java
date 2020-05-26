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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Hashtable;

public class signin extends AppCompatActivity {
    EditText signin_name;
    EditText signin_email;
    EditText signin_passowrd;
    Button signin_button;
    Button signin_cancel;
    private FirebaseAuth mAuth;
    String TAG;
    FirebaseDatabase database;


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
                            FirebaseUser user=mAuth.getCurrentUser();
                            DatabaseReference myRef = database.getReference("users").child(user.getUid());
                            Hashtable<String, String> member
                                    =new Hashtable<String, String>();
                            member.put("email",user.getEmail());
                            myRef.setValue(member);
                            Log.d(TAG,"cretaeUserWithEmail:success");

                            Toast.makeText(signin.this,"가입성공",Toast.LENGTH_SHORT).show();

                            finish();

                        }
                        else {
                            Log.w(TAG,"createUserWithEmail:failure",task.getException());
                            Toast.makeText(signin.this, "이미 존재하는계정입니다.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    public void onStart() {

        super.onStart();
        FirebaseUser currnetUser = mAuth.getCurrentUser();

    }

}
