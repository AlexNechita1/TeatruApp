package com.example.myapplication.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapters.SectionAdapter;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView titluTxt,descTxt,aprecieriTxt,durataTxt,apreciazaBtn;
    private ImageView piesaImg, bkBtn, popupBtn;
    boolean piesaApreciata;
    private String apreciate[];
    private ProgressBar pBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        Bundle extras = getIntent().getExtras();
        String titlu = extras.getString("ITEM_TITLE");
        initView(titlu);

    }





    public boolean arrayContainsString(String[] array, String targetString) {
        for (String element : array) {
            if (element.equals(targetString)) {
                return true;
            }
        }
        return false;
    }

    public void showPopupMenuWithIcon(View view) {
        PopupMenu popup = new PopupMenu(DetailActivity.this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon",boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.detail_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), "You Clicked : " + item.getTitle(),  Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }
    private void initView(String titlu) {
        titluTxt = findViewById(R.id.titluPiesaTxt);
        descTxt = findViewById(R.id.descTxt);
        aprecieriTxt = findViewById(R.id.aprecieriTxt);
        durataTxt = findViewById(R.id.durataTxt);
        piesaImg = findViewById(R.id.piesaImg);
        bkBtn = findViewById(R.id.backBtn);
        apreciazaBtn = findViewById(R.id.aprecieriTxt);
        pBar= findViewById(R.id.pBar);

        popupBtn=findViewById(R.id.popup);
        popupBtn.setOnClickListener(v -> {
            showPopupMenuWithIcon(v);
        });;


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
                                    apreciate=piese;
                                    if(arrayContainsString(piese,titlu)) {
                                        piesaApreciata = true;
                                        aprecieriTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_favorite_24, 0, 0, 0);
                                    }else {
                                        piesaApreciata = false;
                                    }

                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        } else {
            Log.d(TAG, "No user is currently logged in");
        }





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
                                String aprecieri = document.getString("aprecieri");
                                String durata = document.getString("durata");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            Glide.with(piesaImg.getContext())
                                                    .load(downloadUrl)
                                                    .placeholder(R.drawable.fav)
                                                    .error(R.drawable.fav)
                                                    .centerCrop()
                                                    .into(piesaImg);
                                            titluTxt.setText(titlu);
                                            descTxt.setText(descriere);
                                            aprecieriTxt.setText(aprecieri);
                                            durataTxt.setText(durata);
                                            pBar.setVisibility(View.GONE);
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

        String finalUid = uid;
        apreciazaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Piese")
                        .whereEqualTo("den", titlu)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    if (document.exists()) {
                                        String documentId = document.getId();
                                        if(piesaApreciata){
                                            String newValue = Integer.toString(Integer.parseInt(aprecieriTxt.getText().toString()) - 1);
                                            DocumentReference pieseRef = db.collection("Piese").document(documentId);
                                            pieseRef.update("aprecieri", newValue)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            aprecieriTxt.setText(newValue);
                                                            aprecieriTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baselinewhite_favorite_24, 0, 0, 0);
                                                            piesaApreciata=false;

                                                            DocumentReference userRef = db.collection("users").document(finalUid);

                                                            List<String> lista = new ArrayList<>(Arrays.asList(apreciate));
                                                            lista.remove(titlu);

                                                            userRef.update("apreciate",String.join(",",lista));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });

                                        } else {
                                            String newValue = Integer.toString(Integer.parseInt(aprecieriTxt.getText().toString()) + 1);
                                            DocumentReference pieseRef = db.collection("Piese").document(documentId);
                                            pieseRef.update("aprecieri", newValue)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            aprecieriTxt.setText(newValue);
                                                            aprecieriTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_favorite_24, 0, 0, 0);
                                                            piesaApreciata=true;

                                                            DocumentReference userRef = db.collection("users").document(finalUid);

                                                            List<String> lista = new ArrayList<>(Arrays.asList(apreciate));
                                                            lista.add(titlu);

                                                            userRef.update("apreciate",String.join(",",lista));
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                        }
                                                    });
                                        }

                                    } else {
                                        Log.w(TAG, "No such document");
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
        });
        bkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this,MainActivity.class));
            }
        });

    }


}