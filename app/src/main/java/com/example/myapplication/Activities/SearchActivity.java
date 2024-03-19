package com.example.myapplication.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activities.NavBar.AprecieriActivity;
import com.example.myapplication.Adapters.AprecieriAdapter;
import com.example.myapplication.Adapters.SectionAdapter;
import com.example.myapplication.Domian.ApreciateItems;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recycler;
    ImageView bkBtn;
    ProgressBar pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();
        String searchedText = extras.getString("SEARCH_TEXT");
        initView(searchedText);
    }

    private void initView(String searchedText) {
        bkBtn = findViewById(R.id.backBtn);
        pbar = findViewById(R.id.pBar);
        bkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this,MainActivity.class));
            }
        });
        recycler = findViewById(R.id.recycler);
        matches(searchedText);
    }
    private boolean checkSearch(String textToSearch, String textToCompare) {
        textToSearch = Normalizer.normalize(textToSearch, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        textToCompare = Normalizer.normalize(textToCompare, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        return textToCompare.matches("(?i).*" + textToSearch + ".*");
    }

    private void matches(String lastSearch){
        List<String> matchingNames = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Piese")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.getString("den");
                            if (checkSearch(lastSearch,name)) {
                                matchingNames.add(name);
                            }
                        }
                        if(!matchingNames.isEmpty()){
                            getPiese(recycler,matchingNames);
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
                                                view.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
                                                pbar.setVisibility(View.GONE);

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