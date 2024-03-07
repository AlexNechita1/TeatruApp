package com.example.myapplication.Activities;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Activities.NavBar.SettingsActivity;
import com.example.myapplication.Adapters.SliderAdapters;
import com.example.myapplication.Domian.SliderItems;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView.Adapter adapterActiune,adapterComedie,adapterDrama,adapterRomantism;
    private RecyclerView recyclerActiune,recyclerComedie, recyclerDrama, recyclerRomantis;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest,mStringRequest2,mStringrequest3;
    private ProgressBar loading1,loading2,loading3,loading4;
    private ViewPager2 viewPager2;
    private ImageView accountImgView;
    private Handler slideHandler = new Handler();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initView();
            banners();
            sendRequest();
        }

    private void sendRequest() {

    }

    private void banners() {
            List<SliderItems> sliderItems=new ArrayList<>();
            sliderItems.add(new SliderItems(R.drawable.romeo));
            sliderItems.add(new SliderItems(R.drawable.cantareata));
            sliderItems.add(new SliderItems(R.drawable.hamlet));

            viewPager2.setAdapter(new SliderAdapters(sliderItems,viewPager2));
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    float r=1-Math.abs(position);
                    page.setScaleY(0.85f+r*0.15f);
                }
            });
            viewPager2.setPageTransformer(compositePageTransformer);
            viewPager2.setCurrentItem(1);
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
                viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
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
            recyclerActiune=findViewById(R.id.view1);
            recyclerActiune.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            recyclerDrama=findViewById(R.id.view2);
            recyclerDrama.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            recyclerComedie=findViewById(R.id.view3);
            recyclerComedie.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            recyclerRomantis=findViewById(R.id.view4);
            recyclerRomantis.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            loading1=findViewById(R.id.progressBar1);
            loading2=findViewById(R.id.progressBar2);
            loading3=findViewById(R.id.progressBar3);
            loading4=findViewById(R.id.progressBar4);

            viewPager2=findViewById(R.id.viewpagerSlider);
            accountImgView=findViewById(R.id.imageAccount);
            accountImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });



        }
}