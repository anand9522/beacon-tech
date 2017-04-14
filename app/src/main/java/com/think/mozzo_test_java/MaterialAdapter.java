package com.think.mozzo_test_java;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anand on 18/12/16.
 */


public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder> {

    private ArrayList<String> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case

        public TextView mTextView;
        public ViewHolder(TextView v) {
           super(v);
           mTextView = v;
        }
    }

    public MaterialAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder((TextView) v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(mDataset.get(position));

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
