package com.example.stockapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class StockSection extends Section {
    /**
     * Create a Section object based on {@link SectionParameters}.
     *
     * @param sectionParameters section parameters
     */

    private String mHeader;
    private ArrayList<StockItem> mStockList;

    public StockSection(@NonNull String header, ArrayList<StockItem> list, Context context) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.stock_item)
                .headerResourceId(R.layout.section_header)
                .build());

        mHeader = header;
        mStockList = list;
    }

    @Override
    public int getContentItemsTotal() {
        return mStockList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        StockItem currentItem = mStockList.get(position);
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        if (mHeader.matches("PORTFOLIO") && position == 0){
                // Showing the net worth value only for the first row:
                itemHolder.netWorthView.setVisibility(View.VISIBLE);
                itemHolder.tickerView.setText(currentItem.getNetWorth());
                itemHolder.sharesView.setVisibility(View.GONE);
                itemHolder.priceView.setVisibility(View.GONE);
                itemHolder.trendView.setVisibility(View.GONE);
                itemHolder.changeView.setVisibility(View.GONE);
                itemHolder.goToButton.setVisibility(View.GONE);
        }else{
            // Showing normal stock items
            itemHolder.netWorthView.setVisibility(View.GONE);

            //Ticker name
            itemHolder.tickerView.setText(currentItem.getTicker());

            // Show shares or company name
            if (currentItem.getNumShares() == null){
                itemHolder.sharesView.setText(currentItem.getCompanyName());
            }else{
                itemHolder.sharesView.setText(String.format("%s shares", currentItem.getNumShares()));
            }

            // Show current price
            itemHolder.priceView.setText(currentItem.getLastPrice());

            // Show change number
            itemHolder.changeView.setText(String.format("%.2f", Math.abs(currentItem.getChange())));
               // <color name="downTrend">#993f47</color>
               // <color name="upTrend">#319A5C</color>

            // Set trending color and image
            if (currentItem.getChange() > 0){
                itemHolder.changeView.setTextColor(Color.parseColor("#319A5C"));
                itemHolder.trendView.setImageResource(R.drawable.ic_twotone_trending_up_24);
            }else{
                itemHolder.changeView.setTextColor(Color.parseColor("#993f47"));
                itemHolder.trendView.setImageResource(R.drawable.ic_baseline_trending_down_24);

            }

        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewholder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        HeaderViewholder headerHolder = (HeaderViewholder) holder;

        headerHolder.headerView.setText(mHeader);
    }

}
