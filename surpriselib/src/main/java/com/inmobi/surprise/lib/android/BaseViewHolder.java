package com.inmobi.surprise.lib.android;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by davendar.ojha on 7/1/16.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setData(ViewAdapter viewAdapter, Context context, int currentPosition, Typeface customTypeFace);
}
