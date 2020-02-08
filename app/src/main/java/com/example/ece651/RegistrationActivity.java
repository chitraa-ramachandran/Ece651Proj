package com.example.ece651;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity
{
    private EditText mEmail;
    private EditText mPass;
    private Button btnReg;
    private TextView mSignin;

    private ProgressDialog mDialogue;
    //firebase inclusion
    private FirebaseAuth mAuth;


    @Override
    //oncreate is our main method and it references the registration page
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth=FirebaseAuth.getInstance();
        mDialogue=new ProgressDialog(this);
        registration();
    }
    private void registration()
    {
        mEmail = findViewById(R.id.email_login_reg);
        mPass = findViewById(R.id.password_login_reg);
        btnReg = findViewById(R.id.btn_registrate);
        mSignin = findViewById(R.id.sign_in_here);

        btnReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = mEmail.getText().toString().trim();
                String pswd = mPass.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(pswd))
                {
                    mPass.setError("Password is required!");
                }
                mDialogue.setMessage(("Processing....."));
                mAuth.createUserWithEmailAndPassword(email,pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            mDialogue.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration sucessfully completed!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else
                        {
                            mDialogue.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}

