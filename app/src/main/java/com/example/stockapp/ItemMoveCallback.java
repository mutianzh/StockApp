//package com.example.stockapp;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class ItemMoveCallback extends ItemTouchHelper.Callback {
//
//    private final ItemTouchHelperContract mAdapter;
//
//    public ItemMoveCallback(ItemTouchHelperContract adapter) {
//        mAdapter = adapter;
//    }
//
//    @Override
//    public boolean isLongPressDragEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled() {
//        return false;
//    }
//
//
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//
//    }
//
//    @Override
//    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        return makeMovementFlags(dragFlags, 0);
//    }
//
//    @Override
//    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                          RecyclerView.ViewHolder target) {
//        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//        return true;
//    }
//
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
//                                  int actionState) {
//
//
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            if (viewHolder instanceof RecyclerViewAdapter.MyViewHolder) {
//                RecyclerViewAdapter.MyViewHolder myViewHolder=
//                        (RecyclerViewAdapter.MyViewHolder) viewHolder;
//                mAdapter.onRowSelected(myViewHolder);
//            }
//
//        }