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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.ads.AdChoicesView;
import com.squareup.picasso.Picasso;

/**
 * Created by davendar.ojha on 7/1/16.
 */
public class WideViewHolder extends BaseViewHolder {
    ImageView iv;
    DisplayMetrics displayMetrics;
    RelativeLayout rlWideCardParent;
    RelativeLayout belowContent;
    TextView app_name;
    TextView cta;
    GridLayoutManager.LayoutParams rlParentParams;
    RelativeLayout.LayoutParams ivParams;
    RelativeLayout.LayoutParams belowParams;
    RelativeLayout.LayoutParams ctaParam;
    LinearLayout adChoicesIcon;
    int previousPosition = -1;


    public WideViewHolder(View itemView, DisplayMetrics dm) {
        super(itemView);
        iv = (ImageView) itemView.findViewById(R.id.ivWide);
        app_name = (TextView) itemView.findViewById(R.id.app_name);
        cta = (TextView) itemView.findViewById(R.id.cta);
        rlWideCardParent = (RelativeLayout) itemView.findViewById(R.id.rlWideCardParent);
        belowContent = (RelativeLayout) itemView.findViewById(R.id.belowContent);
        adChoicesIcon = (LinearLayout) itemView.findViewById(R.id.adChoicesIcon);
        displayMetrics = dm;
        rlParentParams = (GridLayoutManager.LayoutParams) rlWideCardParent.getLayoutParams();
        ivParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        belowParams = (RelativeLayout.LayoutParams) belowContent.getLayoutParams();
        ctaParam = (RelativeLayout.LayoutParams) cta.getLayoutParams();
    }

    @Override
    public void setData(ViewAdapter viewAdapter, Context context, int currentPosition, Typeface customTypeFace) {
        try {
            final SurpriseAd data = viewAdapter.getItem(currentPosition);
            int spacing = context.getResources().getDimensionPixelOffset(R.dimen.card_spacing);
            int cardWidth = displayMetrics.widthPixels - spacing;
            rlParentParams.width = cardWidth;
            ivParams.width = cardWidth;
            belowParams.width = cardWidth;
            setLayoutsHeight(data.getAspectRatio());
            //Speeding up image loading with redundant check Please benchmark before removing
            rlWideCardParent.setLayoutParams(rlParentParams);
            iv.setLayoutParams(ivParams);
            Picasso.with(context).load(data.url).into(iv);
            belowContent.setLayoutParams(belowParams);
            unregisterOldAds(viewAdapter, currentPosition);
            if (null != data.inMobiNative) {
                adChoicesIcon.setVisibility(View.GONE);
                InMobiNative.bind(rlWideCardParent, data.inMobiNative);
            } else {
                if (adChoicesIcon.getChildCount() == 0) {
                    adChoicesIcon.removeAllViews();
                    adChoicesIcon.addView(new AdChoicesView(context, data.fanNativeAd, true));
                }
                adChoicesIcon.setVisibility(View.VISIBLE);
                data.fanNativeAd.registerViewForInteraction(rlWideCardParent);
            }
            //app_name.setTypeface(customTypeFace);
            cta.setText(data.cta);
            app_name.setText(data.appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterOldAds(ViewAdapter viewAdapter, int currentPosition) {
        if (previousPosition != -1) {
            if (previousPosition != currentPosition) {
                SurpriseAd oldAd = viewAdapter.getItem(previousPosition);
                if (null != oldAd.fanNativeAd) {
                    oldAd.fanNativeAd.unregisterView();
                } else if (null != oldAd.inMobiNative) {
                    InMobiNative.unbind(rlWideCardParent);
                }
                previousPosition = currentPosition;
            }
        } else {
            previousPosition = currentPosition;
        }
    }

    private void setLayoutsHeight(float aspectRatio) {
        ivParams.height = (int) ((ivParams.width / aspectRatio));
        belowParams.height = (int) (displayMetrics.heightPixels * 0.07);
        rlParentParams.height = (int) (rlParentParams.width / aspectRatio);
        rlParentParams.height = rlParentParams.height + belowParams.height;
        ctaParam.height = belowParams.height;
        cta.setLayoutParams(ctaParam);
    }
}
