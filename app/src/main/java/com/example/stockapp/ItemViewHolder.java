package com.example.stockapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    ConstraintLayout layoutView;
    TextView netWorthView;
    TextView tickerView;
    TextView sharesView;
    TextView priceView;
    ImageView trendView;
    TextView changeView;
    Button goToButton;

    public ItemViewHolder(@NonNull View itemView, StockSection.OnItemClickListener listener) {
        super(itemView);
        layoutView = itemView.findViewById(R.id.itemView);
        netWorthView = itemView.findViewById(R.id.netWorth);
        tickerView = itemView.findViewById(R.id.ticker);
        sharesView = itemView.findViewById(R.id.sharesOrName);
        priceView = itemView.findViewById(R.id.lastPrice);
        trendView = itemView.findViewById(R.id.trendImage);
        changeView = itemView.findViewById(R.id.change);
        goToButton =itemView.findViewById(R.id.goToButton);

        goToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    listener.onGoTo(position);
                }
            }
        });

    }
}
