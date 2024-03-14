package com.example.myapplication.Adapters;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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
import com.example.myapplication.Domian.ApreciateItems;
import com.example.myapplication.Domian.RecyclerItems;
import com.example.myapplication.R;

import java.util.List;

public class AprecieriAdapter extends RecyclerView.Adapter<AprecieriAdapter.SectionViewHolder>{


    private List<ApreciateItems> itemList;

    // Constructor to initialize the item list
    public AprecieriAdapter(List<ApreciateItems> itemList) {
        this.itemList = itemList;
    }
    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txTitle,txGen,txDurata;

        public SectionViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgTxt);
            txTitle = itemView.findViewById(R.id.titluTxt);
            txGen = itemView.findViewById(R.id.genTxt);
            txDurata = itemView.findViewById(R.id.durataTxt);
        }
    }

    @NonNull
    @Override
    public AprecieriAdapter.SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loved_item, parent, false);
        return new AprecieriAdapter.SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AprecieriAdapter.SectionViewHolder holder, int position) {
        ApreciateItems item = itemList.get(position);

        holder.txTitle.setText(item.getTitlu());
        holder.txGen.setText(item.getGen());
        holder.txDurata.setText(item.getDurata());

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
                bundle.putString("ITEM_TITLE", item.getTitlu());


                intent.putExtras(bundle);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
