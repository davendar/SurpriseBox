package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.ads.AdSlot;
import com.inmobi.surprise.lib.ads.SlotManager;
import com.inmobi.surprise.lib.ads.SurpriseAd;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by davendar.ojha on 7/1/16.
 */
public class ViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<SurpriseAd> imageList;
    WeakReference<Context> context;
    DisplayMetrics displayMetrics;
    SlotManager slotManager;
    Typeface customTypeFace;


    public ViewAdapter(Context context, List<SurpriseAd> imageList,
                       DisplayMetrics displayMetrics,
                       SlotManager slotManager, Typeface customTypeFace) {
        this.imageList = imageList;
        this.context = new WeakReference<>(context);
        this.displayMetrics = displayMetrics;
        this.slotManager = slotManager;
        this.customTypeFace = customTypeFace;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case AdSlot.BIG_CARD:
                viewHolder = getWideViewHolder(parent);
                break;
            case AdSlot.SMALL_CARD:
                viewHolder = getSimpleViewHolder(parent);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(this, context.get(), position, customTypeFace);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public WideViewHolder getWideViewHolder(ViewGroup parent) {
        View itemView;
        WideViewHolder viewHolder;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wide_card, parent, false);
        viewHolder = new WideViewHolder(itemView, displayMetrics);
        return viewHolder;
    }


    public SimpleViewHolder getSimpleViewHolder(ViewGroup parent) {
        View itemView;
        SimpleViewHolder viewHolder;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        viewHolder = new SimpleViewHolder(itemView, displayMetrics);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return slotManager.getSlotType(position);
    }

    public SurpriseAd getItem(int position) {
        return imageList.get(position);
    }
}
