package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Database;
import com.example.myapplication.MySingleton;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
private EditText userEdt, passEdt;
private Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Database dbReader = MySingleton.getInstance().getMyObject();

        checkLogin(dbReader);

    }

    private void checkLogin(Database dbReader) {
        userEdt=findViewById(R.id.editTextUser);
        passEdt=findViewById(R.id.editTextPassword);
        loginBtn=findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {



            //Toast.makeText(LoginActivity.this, userInfo.toString() +" " +userEdt.getText().toString(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(LoginActivity.this, userInfo.length, Toast.LENGTH_SHORT).show();

            if(userEdt.getText().toString().isEmpty() || passEdt.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Introduceti numele de utilizator si parola", Toast.LENGTH_SHORT).show();
            } else if(dbReader.checkUsername(userEdt.getText().toString().trim())){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Numele de utilizator incorect", Toast.LENGTH_SHORT).show();
            }
            Log.w("User",userEdt.getText().toString().trim());
            /*if(userEdt.getText().toString().isEmpty() || passEdt.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this,"Introduceti numele de utilizator si parola",Toast.LENGTH_SHORT).show();
            }else if(userInfo.get(1).toString().trim().equals("0")){
                Toast.makeText(LoginActivity.this,"Numele de utilizator incorect",Toast.LENGTH_SHORT).show();
            }else if(userInfo.get(1).toString().trim().equals(userEdt.getText().toString()) || !userInfo.get(2).toString().trim().equals(passEdt.getText().toString())){
                Toast.makeText(LoginActivity.this,"Parola incorecta",Toast.LENGTH_SHORT).show();
            }else if(userInfo.get(1).toString().trim().equals(userEdt.getText().toString()) || userInfo.get(2).toString().trim().equals(passEdt.getText().toString())){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }*/
        });



    }


}