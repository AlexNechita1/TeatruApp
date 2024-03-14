package com.example.myapplication.Activities.NavBar;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.DetailActivity;
import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Adapters.AprecieriAdapter;
import com.example.myapplication.Adapters.SectionAdapter;
import com.example.myapplication.Domian.ApreciateItems;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AprecieriActivity extends AppCompatActivity {
    RecyclerView recycler;
    ImageView bkBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprecieri);
        initView();
    }

    private void initView() {

        bkBtn = findViewById(R.id.backBtn);
        bkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AprecieriActivity.this,MainActivity.class));
            }
        });



        recycler = findViewById(R.id.recycler);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = "";

        if (currentUser != null) {
            uid = currentUser.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String aprecieri = document.getString("apreciate");
                                    String[] piese = aprecieri.split(",");
                                    List<String> lista = new ArrayList<>(Arrays.asList(piese));
                                    getPiese(recycler,lista);
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        } else {
            Log.d(TAG, "No user is currently logged in");
        }



    }
    private void getPiese(RecyclerView view, List<String> lista) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Piese")
                .whereIn("den", lista)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ApreciateItems> section = new ArrayList<>();
                        //Log.d(TAG, "query document snapshots size: "+queryDocumentSnapshots.size());
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.exists()) {
                                String name = document.getString("den");
                                String imageUrl = document.getString("url");
                                String gen = document.getString("gen");
                                String durata = document.getString("durata");

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            //Log.e(TAG,"ce se trimite: "+name+" "+gen+ " "+durata+" "+downloadUrl);
                                            section.add(new ApreciateItems(name,gen,durata,downloadUrl));

                                            if (section.size() == queryDocumentSnapshots.size()) {

                                                AprecieriAdapter adapter = new AprecieriAdapter(section);
                                                view.setAdapter(adapter);
                                                view.setLayoutManager(new LinearLayoutManager(AprecieriActivity.this, LinearLayoutManager.VERTICAL, false));

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error getting download URL", e);
                                        }
                                    });
                                } else {
                                    Log.w(TAG, "Empty or null imageUrl for document: " + name);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting documents", e);
                    }
                });
    }
}