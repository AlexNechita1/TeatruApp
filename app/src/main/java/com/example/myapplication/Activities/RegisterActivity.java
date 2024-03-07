package com.example.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private TextView loginTxt;
    private EditText mailEdt, passEdt;
    private Button registerBtn;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        Spinner spinner = (Spinner) findViewById(R.id.SpinnerAdresa);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.planets_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void initView() {
        mailEdt=findViewById(R.id.editTextMail);
        passEdt=findViewById(R.id.editTextPassword);
        registerBtn=findViewById(R.id.registerBtn);
        loginTxt=findViewById(R.id.TextViewLogin);
        progressBar=findViewById(R.id.progressRegister);

        mAuth=FirebaseAuth.getInstance();

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = mailEdt.getText().toString();
                password = passEdt.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Introduceti E-mail si parola", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "Cont creat cu succes", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });

    }
}