package com.nuzharukiya.authlauncher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuzharukiya.authlauncher.R;
import com.nuzharukiya.authlauncher.model.CherryModel;

import java.util.ArrayList;

/**
 * Created by Nuzha Rukiya on 18/01/10.
 */

public class CherryAdapter extends RecyclerView.Adapter<CherryAdapter.CherryHolder> {

    private Context context;
    private ArrayList<CherryModel> cherriesList;

    public CherryAdapter(Context context, ArrayList<CherryModel> mCardItems) {
        this.context = context;
        this.cherriesList = mCardItems;
    }

    @Override
    public CherryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CherryHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_cherry_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CherryHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cherriesList == null ? 0 : cherriesList.size();
    }

    class CherryHolder extends RecyclerView.ViewHolder {

        View cardBase;
        ImageView ivBackground;
        TextView tvCherryName;

        CherryHolder(View itemView) {
            super(itemView);

            cardBase = itemView;
            ivBackground = itemView.findViewById(R.id.ivBackground);
            tvCherryName = itemView.findViewById(R.id.tvCherryName);
        }

        void bind(int position) {

            final CherryModel item = cherriesList.get(position);

            tvCherryName.setText(item.getCherryTitle());
            ivBackground.setBackgroundColor(context.getResources().getColor(item.getCherryBackground()));
//            ivBackground.setImageResource(item.getCherryBackground());
        }
    }
}
