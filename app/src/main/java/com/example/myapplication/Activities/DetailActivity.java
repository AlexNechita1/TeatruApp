package com.example.myapplication.Activities;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapters.SectionAdapter;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView titluTx,descTx;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titluTx = findViewById(R.id.titluTxt);
        img = findViewById(R.id.imgView);
        descTx = findViewById(R.id.descTxt);

        Bundle extras = getIntent().getExtras();
        String titlu = extras.getString("ITEM_TITLE");
        if (extras != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Piese")
                    .whereEqualTo("den", titlu)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<RecyclerItems> section = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                if (document.exists()) {
                                    String imageUrl = document.getString("url");
                                    String descriere = document.getString("desc");

                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Log.d(TAG, titlu + " " + imageUrl);
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();


                                                Glide.with(img.getContext())
                                                        .load(downloadUrl)
                                                        .placeholder(R.drawable.fav)
                                                        .error(R.drawable.fav)
                                                        .centerCrop()
                                                        .into(img);
                                                titluTx.setText(titlu);
                                                descTx.setText(descriere);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error getting download URL", e);
                                            }
                                        });
                                    } else {
                                        Log.w(TAG, "Empty or null imageUrl for document: " + titlu);
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
}