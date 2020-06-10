package com.example.trip2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import xyz.hasnat.sweettoast.SweetToast;

public class SignUpActivity extends AppCompatActivity {

    private Button   createAccountButton;
    private EditText userEmail, userPassword,userPasswordConfirm;
    private TextView alreadyHaveAccount;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private FirebaseUser currentUser;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;

    private static final String TAG = "SignUpActivity";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth=FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        createAccountButton = (Button)findViewById(R.id.signup_button);
        userEmail = (EditText)findViewById(R.id.signup_email);
        userPassword = (EditText)findViewById(R.id.signup_password);
        userPasswordConfirm=(EditText)findViewById(R.id.signup_confirm);

        alreadyHaveAccount = (TextView)findViewById(R.id.already_have_account);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String confirmPassword=userPasswordConfirm.getText().toString();
                CreateNewAccount(email, password, confirmPassword);
            }
        });

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        progressDialog = new ProgressDialog(SignUpActivity.this);
    }



    private void CreateNewAccount(String email, String password, String confirmPassword) {


        if (TextUtils.isEmpty(email)){
            SweetToast.error(SignUpActivity.this, "Your email is required.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SweetToast.error(SignUpActivity.this, "Your email is not valid.");
        }
        else if (TextUtils.isEmpty(password)){
            SweetToast.error(SignUpActivity.this, "Please fill this password field");
        } else if (password.length() < 6){
            SweetToast.error(SignUpActivity.this, "Create a password at least 6 characters long.");
        }else if (TextUtils.isEmpty(confirmPassword)){
            SweetToast.warning(SignUpActivity.this, "Please retype in password field");
        } else if (!password.equals(confirmPassword)){
            SweetToast.error(SignUpActivity.this, "Your password don't match with your confirm password");

        } else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Map<String, Object> Userinfo = new HashMap<>();
                                String deviceToken = String.valueOf(FirebaseInstanceId.getInstance().getInstanceId());
                                String currentUserID = mAuth.getCurrentUser().getUid();



                                Userinfo.put("verified","false");
                                Userinfo.put("deviceToken", deviceToken);

                                db.collection("Users").document(currentUserID).set(Userinfo, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                                    currentUser = mAuth.getCurrentUser();
                                                    if (currentUser != null){
                                                        currentUser.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            registerSuccessPopUp();

                                                                            // LAUNCH activity after certain time period
                                                                            new Timer().schedule(new TimerTask(){
                                                                                public void run() {
                                                                                    SignUpActivity.this.runOnUiThread(new Runnable() {
                                                                                        public void run() {
                                                                                            mAuth.signOut();

                                                                                            Intent mainIntent =  new Intent(SignUpActivity.this, LoginActivity.class);
                                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(mainIntent);
                                                                                            finish();

                                                                                            SweetToast.info(SignUpActivity.this, "Please check your email & verify.");

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }, 8000);


                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                }

                                            }
                                        });

                                /* realtimeDB ver
                                rootRef.child("Users").child(currentUserID).setValue("");
                                rootRef.child("Users").child(currentUserID).child("deviceToken")
                                        .setValue(deviceToken);
                                */


                            }
                            else {
                                String message = task.getException().getMessage();
                                SweetToast.error(SignUpActivity.this, "Error occurred : " + message);
                            }
                            progressDialog.dismiss();
                        }
                    });
            //config progressbar
            progressDialog.setTitle(R.string.accounting);
            progressDialog.setMessage(R.string.wait+"");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

        }
    }



    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
    private void registerSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        View view = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.register_success_popup, null);

        //ImageButton imageButton = view.findViewById(R.id.successIcon);
        //imageButton.setImageResource(R.drawable.logout);
        builder.setCancelable(false);

        builder.setView(view);
        builder.show();
    }

}
