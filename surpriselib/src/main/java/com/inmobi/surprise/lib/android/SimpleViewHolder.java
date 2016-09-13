package com.inmobi.surprise.lib.android;

import com.inmobi.ads.InMobiNative;
import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.ads.SurpriseAd;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by davendar.ojha on 7/1/16.
 */
public class SimpleViewHolder extends BaseViewHolder {
    ImageView iv;
    RelativeLayout rlSimpleCardParent;
    RelativeLayout belowContent;
    DisplayMetrics displayMetrics;
    GridLayoutManager.LayoutParams rlParentParams;
    RelativeLayout.LayoutParams ivParams;
    RelativeLayout.LayoutParams belowParams;
    TextView app_name;
    int previousPosition = -1;


    public SimpleViewHolder(View itemView, DisplayMetrics dm) {
        super(itemView);
        iv = (ImageView) itemView.findViewById(R.id.ivImage);
        rlSimpleCardParent = (RelativeLayout) itemView.findViewById(R.id.rlSimpleCardParent);
        belowContent = (RelativeLayout) itemView.findViewById(R.id.belowContent);
        app_name = (TextView) itemView.findViewById(R.id.app_name);
        this.displayMetrics = dm;
        rlParentParams = (GridLayoutManager.LayoutParams) rlSimpleCardParent.getLayoutParams();
        ivParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        belowParams = (RelativeLayout.LayoutParams) belowContent.getLayoutParams();
    }

    @Override
    public void setData(ViewAdapter viewAdapter, Context context, int currentPosition, Typeface customTypeFace) {
        try {
            final SurpriseAd data = viewAdapter.getItem(currentPosition);
            int spacing = context.getResources().getDimensionPixelOffset(R.dimen.card_spacing) / 2;
            int halfScreen = displayMetrics.widthPixels / 2;
            int cardWidth = halfScreen - spacing;
            rlParentParams.width = cardWidth;
            ivParams.width = cardWidth;
            belowParams.width = cardWidth;
            setLayoutsHeight(data.getAspectRatio());
            rlSimpleCardParent.setLayoutParams(rlParentParams);
            iv.setLayoutParams(ivParams);
            Picasso.with(context).load(data.url).into(iv);
            belowContent.setLayoutParams(belowParams);
            unregisterOldFanAd(viewAdapter, currentPosition);
            if (null != data.inMobiNative) {
                InMobiNative.bind(rlSimpleCardParent, data.inMobiNative);
            } else {
                data.fanNativeAd.registerViewForInteraction(rlSimpleCardParent);
            }
            //app_name.setTypeface(customTypeFace);
            app_name.setText(data.appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterOldFanAd(ViewAdapter viewAdapter, int currentPosition) {
        if (previousPosition != -1) {
            if (previousPosition != currentPosition) {
                SurpriseAd oldAd = viewAdapter.getItem(previousPosition);
                if (null != oldAd.fanNativeAd) {
                    oldAd.fanNativeAd.unregisterView();
                } else if (null != oldAd.inMobiNative) {
                    InMobiNative.unbind(rlSimpleCardParent);
                }
                previousPosition = currentPosition;
            }
        } else {
            previousPosition = currentPosition;
        }
    }

    private void setLayoutsHeight(float aspectRatio) {
        ivParams.height = (int) (ivParams.width / aspectRatio);
        belowParams.height = (int) (displayMetrics.heightPixels * 0.07);
        rlParentParams.height = (int) (rlParentParams.width / aspectRatio);
        rlParentParams.height = rlParentParams.height + belowParams.height;
        ivParams.setMargins(0, 0, 0, 0);
    }
}
