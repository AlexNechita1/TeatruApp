package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Adapters.SliderAdapters;
import com.example.myapplication.Domian.SliderItems;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView.Adapter adapterBestMovies;

    private ViewPager2 viewPager2;
    private ImageView accountImgView;
    private Handler slideHandler = new Handler();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initView();
            banners();
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
            viewPager2=findViewById(R.id.viewpagerSlider);
            accountImgView=findViewById(R.id.imageAccount);

        }
}