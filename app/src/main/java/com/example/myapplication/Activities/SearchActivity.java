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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();
        String searchedText = extras.getString("SEARCH_TEXT");
        initView(searchedText);
    }

    private void initView(String search) {
        bkBtn = findViewById(R.id.backBtn);
        bkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this,MainActivity.class));
            }
        });
        Log.d(TAG,"searched ttext: "+search);
        recycler = findViewById(R.id.recycler);
        getPiese(recycler,search);
    }
    private boolean checkSearch(String textToSearch, String textToCompare) {

        textToSearch = Normalizer.normalize(textToSearch, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        textToCompare = Normalizer.normalize(textToCompare, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        return textToCompare.contains(textToSearch);
    }


    private String convertToASCII(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            builder.append((int) ch);
        }
        return builder.toString();
    }
    private void getPiese(RecyclerView view, String searchedText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Piese")

                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ApreciateItems> section = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.exists()) {
                                String name = document.getString("den");
                                String imageUrl = document.getString("url");
                                String gen = document.getString("gen");
                                String durata = document.getString("durata");
                                Log.d(TAG,"nume: "+name.trim().toLowerCase()+" cautare: "+searchedText.trim().toLowerCase()+" marime nume: "+name.length()+" marime cautare: "+searchedText.length());
                                if(checkSearch(searchedText,name)){
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();
                                                section.add(new ApreciateItems(name, gen, durata, downloadUrl));

                                                if (section.size() == queryDocumentSnapshots.size()) {
                                                    AprecieriAdapter adapter = new AprecieriAdapter(section);
                                                    view.setAdapter(adapter);
                                                    view.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
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
                                    Toast.makeText(SearchActivity.this, "Nu s-a gasit nimic :(", Toast.LENGTH_SHORT).show();
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