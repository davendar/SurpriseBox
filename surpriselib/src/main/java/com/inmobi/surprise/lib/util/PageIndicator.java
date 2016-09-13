package com.inmobi.surprise.lib.util;

/**
 * Created by davendar.ojha on 2/24/16.
 */

import android.support.v4.view.ViewPager;

public interface PageIndicator extends ViewPager.OnPageChangeListener {

    void setViewPager(ViewPager view);

    void setViewPager(ViewPager view, int initialPosition);

    void setCurrentItem(int item);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    void notifyDataSetChanged();
}
