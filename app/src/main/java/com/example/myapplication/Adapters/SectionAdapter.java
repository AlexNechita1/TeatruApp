package com.example.myapplication.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.DetailActivity;
import com.example.myapplication.Activities.LoginActivity;
import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.R;

import java.util.List;


import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private List<RecyclerItems> itemList;

    // Constructor to initialize the item list
    public SectionAdapter(List<RecyclerItems> itemList) {
        this.itemList = itemList;
    }

    // Inner ViewHolder class
    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            txTitle = itemView.findViewById(R.id.txTitle);
        }
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        RecyclerItems item = itemList.get(position);

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.imageView.getContext())
                    .load(item.getImageUrl())
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.hamlet);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("ITEM_TITLE", item.getTitle());


                intent.putExtras(bundle);

                v.getContext().startActivity(intent);
            }
        });
        holder.txTitle.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
