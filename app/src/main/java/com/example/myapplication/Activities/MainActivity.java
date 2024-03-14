package com.example.myapplication.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.Activities.NavBar.AprecieriActivity;
import com.example.myapplication.Activities.NavBar.SettingsActivity;
import com.example.myapplication.Adapters.SectionAdapter;
import com.example.myapplication.Adapters.SliderAdapters;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.Domian.SliderItems;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerComedie, recyclerDrama, recyclerRomantis;

    private ProgressBar  loadingDrama, loadingRomantism, loadingComedie;
    private ViewPager2 mainSlider;
    private ImageView accountImgView, aprecieriImgView, acasaImgView;
    private EditText search;

    private Handler slideHandler = new Handler();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initView();
            getTop();
            initSections();

        }

    private void initSections() {

        //Drama
        getPiese(recyclerDrama,"Drama",loadingDrama);

        //Comedie
        getPiese(recyclerComedie,"Comedie",loadingComedie);

        //Romamtism
        getPiese(recyclerRomantis,"Romantism",loadingRomantism);
    }

    private void getPiese(RecyclerView view, String gen, ProgressBar pBar) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Piese")
                .whereEqualTo("gen", gen)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<RecyclerItems> section = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.exists()) {
                                String name = document.getString("den");
                                String imageUrl = document.getString("url");

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            section.add(new RecyclerItems(name, downloadUrl));

                                            if (section.size() == queryDocumentSnapshots.size()) {
                                                SectionAdapter adapter = new SectionAdapter(section);
                                                view.setAdapter(adapter);
                                                view.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                                pBar.setVisibility(View.GONE);
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
    private void getTop(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Piese")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<SliderItems> section = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.exists()) {
                                String imageUrl = document.getString("url");

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            section.add(new SliderItems(downloadUrl));

                                            if (section.size() == queryDocumentSnapshots.size()) {
                                                banners(section);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error getting download URL", e);
                                        }
                                    });
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



    private void banners(List<SliderItems> sliderItems) {
            //List<SliderItems> sliderItems=getTop();
            /*sliderItems.add(new SliderItems(R.drawable.romeo));
            sliderItems.add(new SliderItems(R.drawable.cantareata));
            sliderItems.add(new SliderItems(R.drawable.hamlet));*/

            mainSlider.setAdapter(new SliderAdapters(sliderItems, mainSlider));
            mainSlider.setClipToPadding(false);
            mainSlider.setClipChildren(false);
            mainSlider.setOffscreenPageLimit(3);
            mainSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    float r=1-Math.abs(position);
                    page.setScaleY(0.85f+r*0.15f);
                }
            });
            mainSlider.setPageTransformer(compositePageTransformer);
            mainSlider.setCurrentItem(1);
            mainSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    slideHandler.removeCallbacks(slideRunnable);
                }
            });
        }
        private Runnable slideRunnable=new Runnable() {
            @Override
            public void run() {
                mainSlider.setCurrentItem(mainSlider.getCurrentItem()+1);
            }
        };

        @Override
        protected void onPause() {
            super.onPause();
            slideHandler.removeCallbacks(slideRunnable);
        }

        @Override
        protected void onResume() {
            super.onResume();
            slideHandler.postDelayed(slideRunnable,2000);
        }

        private void initView() {
            mainSlider =findViewById(R.id.viewpagerSlider);
            accountImgView=findViewById(R.id.imageAccount);
            aprecieriImgView=findViewById(R.id.imageAprecieri);
            acasaImgView=findViewById(R.id.imageAcasa);

            recyclerComedie=findViewById(R.id.viewComedie);
            recyclerDrama=findViewById(R.id.viewDrama);
            recyclerRomantis=findViewById(R.id.viewRomantism);

            loadingDrama=findViewById(R.id.barDrama);
            loadingComedie=findViewById(R.id.barComedie);
            loadingRomantism=findViewById(R.id.barRomantism);

            search=findViewById(R.id.editSearch);
            search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                        Intent intent = new Intent(v.getContext(), SearchActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("SEARCH_TEXT", search.getText().toString());


                        intent.putExtras(bundle);

                        v.getContext().startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });



            accountImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });

            aprecieriImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, AprecieriActivity.class));
                }
            });

            acasaImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            });





        }
}