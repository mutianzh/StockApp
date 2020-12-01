package com.example.stockapp;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewsCardAdapter extends RecyclerView.Adapter<NewsCardAdapter.NewsCardViewHolder>{
    private ArrayList<NewsCardItem> mNewsCardList;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mLongClickListener = listener;
    }


    public static class NewsCardViewHolder extends RecyclerView.ViewHolder{
        public ImageView newsimage;
        public TextView newssource;
        public TextView newstimestamp;
        public TextView newstitle;

        public NewsCardViewHolder(@NonNull View itemView, OnItemClickListener listener1, OnItemLongClickListener listener2) {
            super(itemView);
            newsimage = itemView.findViewById(R.id.newsimage);
            newstimestamp = itemView.findViewById(R.id.newstimestamp);
            newssource = itemView.findViewById(R.id.newssource);
            newstitle = itemView.findViewById(R.id.newstitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener1 != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener1.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener2 != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener2.onItemLongClick(position);
                        }
                    }

                    return true;
                }
            });

        }
    }

    public NewsCardAdapter(ArrayList<NewsCardItem> newsCardList){
        mNewsCardList = newsCardList;
    }

    @NonNull
    @Override
    public NewsCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        NewsCardViewHolder ncv = new NewsCardViewHolder(v, mClickListener, mLongClickListener);
        return ncv;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull NewsCardViewHolder holder, int position) {
        NewsCardItem currentItem = mNewsCardList.get(position);
        if (currentItem.getmImageUrl() != "null"){
            Glide.with(holder.newsimage).load(currentItem.getmImageUrl()).into(holder.newsimage);
        }
        holder.newsimage.setClipToOutline(true);
        holder.newstitle.setText(currentItem.getmTitle());
        holder.newstimestamp.setText(currentItem.getmTimeGap());
        holder.newssource.setText(currentItem.getmSource());
    }

    @Override
    public int getItemCount() {
        return mNewsCardList.size();
    }
}
