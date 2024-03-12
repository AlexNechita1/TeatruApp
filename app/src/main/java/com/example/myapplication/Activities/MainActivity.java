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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView.Adapter adapterActiune,adapterComedie,adapterDrama,adapterRomantism;
    private RecyclerView recyclerActiune,recyclerComedie, recyclerDrama, recyclerRomantis;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest,mStringRequest2,mStringrequest3;
    private ProgressBar loadingActiune, loadingDrama, loadingRomantism, loadingComedie;
    private ViewPager2 mainSlider;
    private ImageView accountImgView;

    private Handler slideHandler = new Handler();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initView();
            banners();
            initSections();

        }

    private void initSections() {
        //Actiune
        List<RecyclerItems> section1 = new ArrayList<>();

        SectionAdapter adapter = new SectionAdapter(section1);
        recyclerActiune.setAdapter(adapter);
        recyclerActiune.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        //Drama
        List<RecyclerItems> section2 = new ArrayList<>();

        SectionAdapter adapter2 = new SectionAdapter(section2);
        recyclerDrama.setAdapter(adapter2);
        recyclerDrama.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        //Comedie

        getPiese(recyclerComedie,"Comedie");

        //Actiune
        List<RecyclerItems> section4 = new ArrayList<>();

        SectionAdapter adapter4 = new SectionAdapter(section4);
        recyclerRomantis.setAdapter(adapter);
        recyclerRomantis.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    private void getPiese( RecyclerView view, String gen) {
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
                                    Log.d(TAG, name + " " + imageUrl);
                                    section.add(new RecyclerItems(name, imageUrl));
                                } else {
                                    Log.w(TAG, "Empty or null imageUrl for document: " + name);
                                }


                            } else {
                                Log.w(TAG, "Error getting documents.");
                            }
                            SectionAdapter adapter = new SectionAdapter(section);
                            view.setAdapter(adapter);
                            view.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            loadingComedie.setVisibility(View.GONE);
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



    private void banners() {
            List<SliderItems> sliderItems=new ArrayList<>();
            sliderItems.add(new SliderItems(R.drawable.romeo));
            sliderItems.add(new SliderItems(R.drawable.cantareata));
            sliderItems.add(new SliderItems(R.drawable.hamlet));

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

            recyclerActiune=findViewById(R.id.viewActiune);
            recyclerComedie=findViewById(R.id.viewComedie);
            recyclerDrama=findViewById(R.id.viewDrama);
            recyclerRomantis=findViewById(R.id.viewRomantism);

            loadingActiune=findViewById(R.id.barActiune);
            loadingDrama=findViewById(R.id.barDrama);
            loadingComedie=findViewById(R.id.barComedie);
            loadingRomantism=findViewById(R.id.barRomantism);

            accountImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });



        }
}