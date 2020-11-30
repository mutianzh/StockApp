package com.example.stockapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeaderViewholder extends RecyclerView.ViewHolder {
    TextView headerView;
    public HeaderViewholder(@NonNull View itemView) {
        super(itemView);

        headerView = itemView.findViewById(R.id.sectionHeader);
    }
}
