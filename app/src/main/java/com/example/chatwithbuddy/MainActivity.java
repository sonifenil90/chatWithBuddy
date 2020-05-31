package com.example.chatwithbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    EditText nameTxt,emailTxt,passTxt;
    TextView signupTxt;
    Button button;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    int ls = 0;

    public void getIn(View view){

        if(ls==0){
            mAuth.signInWithEmailAndPassword(emailTxt.getText().toString(), passTxt.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                login();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"There was some problem. Try Again!",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }
        else if(ls==1)
        {
            mAuth.createUserWithEmailAndPassword(emailTxt.getText().toString(), passTxt.getText().toString())
            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("email").setValue(emailTxt.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("name").setValue(nameTxt.getText().toString());
                        login();
                    } else {
                        Toast.makeText(getApplicationContext(), "SignUp Failed! Try Again!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }



    }

    public void login(){

        Intent intent = new Intent(this,UsersList.class);
        intent.putExtra("email",mAuth.getCurrentUser().getEmail());
        startActivity(intent);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mAuth.getCurrentUser() != null)
        {
            login();

        }


        nameTxt = (EditText) findViewById(R.id.nameTxt);
        emailTxt = (EditText) findViewById(R.id.emailTxt);
        passTxt = (EditText) findViewById(R.id.passTxt);
        signupTxt = (TextView) findViewById(R.id.signupTxt);
        button = (Button) findViewById(R.id.button);

        nameTxt.setVisibility(View.INVISIBLE);

        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ls==0) {
                    ls = 1;
                    signupTxt.setText("Already an account? Login!");
                    button.setText("SignUp, and Login!");
                    nameTxt.setVisibility(View.VISIBLE);
                }
                else {
                    ls = 0;
                    signupTxt.setText("or SignUp!");
                    button.setText("Login");
                    nameTxt.setVisibility(View.INVISIBLE);
                }


            }
        });
    }



}
