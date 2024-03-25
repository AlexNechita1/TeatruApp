package com.example.myapplication.Activities.NavBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Activities.IntroActivity;
import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    private Button signoutBtn;
    private ImageView bkBtn;
    private TextView email,uid;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        initView();
    }



    private void initView() {
        signoutBtn=findViewById(R.id.signoutBtn);
        mAuth= FirebaseAuth.getInstance();
        bkBtn=findViewById(R.id.backBtn);
        email=findViewById(R.id.emailTxt);
        uid=findViewById(R.id.uidTxt);


        bkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                startActivity(new Intent(SettingsActivity.this, IntroActivity.class));
            }
        });


        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uidStr,emailStr;
        if (currentUser != null) {
            uidStr = currentUser.getUid();
            emailStr = currentUser.getEmail();
            email.setText(emailStr);
            uid.setText(uidStr);

        }
    }
}