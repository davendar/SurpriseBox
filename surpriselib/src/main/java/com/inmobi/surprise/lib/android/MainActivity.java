package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.ads.AdConsumer;
import com.inmobi.surprise.lib.ads.AdService;
import com.inmobi.surprise.lib.ads.AdSlot;
import com.inmobi.surprise.lib.ads.AdType;
import com.inmobi.surprise.lib.ads.SlotManager;
import com.inmobi.surprise.lib.ads.SurpriseAd;
import com.inmobi.surprise.lib.notification.NotificationScheduler;
import com.inmobi.surprise.lib.util.Constants;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Clear;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

/**
 * Created by davendar.ojha on 7/1/16.
 */
public class MainActivity extends Activity implements AdConsumer {

    @Inject
    AdService adService;

    @Inject
    SlotManager slotManager;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    NotificationScheduler notificationScheduler;


    private RecyclerView rv;
    private TextView tvAppName;
    private ViewAdapter myAdapter;
    private List<SurpriseAd> surpriseAdList = new ArrayList<>();
    private Handler handler;
    private ConcurrentHashMap<String, SurpriseAd> adMap = new ConcurrentHashMap<>();
    private RelativeLayout rlBurgerParent;
    private GridLayoutManager mLayoutManager;
    private final long validity = TimeUnit.MINUTES.toMillis(20);
    GestureDetector mGestureDetector;
    Typeface customTypeFace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injectors.initialize(getApplicationContext());
        Injectors.APP_COMPONENT.inject(this);
        setContentView(R.layout.main);
        handler = new Handler(Looper.getMainLooper());
        setupRecyclerView();
        initComponents();
    }

    private void initComponents() {
        tvAppName = (TextView) findViewById(R.id.tvAppName);
        Typeface surpriseTypeFace = Typeface.createFromAsset(getAssets(), "fonts/coheadline.ttf");
        tvAppName.setText(tvAppName.getText().toString().toLowerCase());
        tvAppName.setTypeface(surpriseTypeFace);
        rlBurgerParent = (RelativeLayout) findViewById(R.id.rlBurgerParent);
        rlBurgerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(MainActivity.this).load(R.drawable.third_frag_background_female).fetch();
                Picasso.with(MainActivity.this).load(R.drawable.third_frag_background_male).fetch();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left_to_right, 0);
            }
        });

    }

    private void setupRecyclerView() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        customTypeFace = Typeface.createFromAsset(getAssets(), "fonts/gotham.ttf");
        myAdapter = new ViewAdapter(this, surpriseAdList, metrics, slotManager, customTypeFace);
        rv = (RecyclerView) findViewById(R.id.rvContainer);
        mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if (slotManager.getSlotType(position) == AdSlot.BIG_CARD) {
                    return 2;
                }
                return 1;
            }
        });
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.card_spacing);
        rv.addItemDecoration(itemDecoration);
        rv.setAdapter(myAdapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastPosition = mLayoutManager.findLastVisibleItemPosition();
                    if (lastPosition == (adMap.size() - 1)) {
                        adService.fetchAd(MainActivity.this);
                    }
                }
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                try {
                    View childView = rv.findChildViewUnder(e.getX(), e.getY());
                    if (childView != null && mGestureDetector.onTouchEvent(e)) {
                        SurpriseAd surpriseAd = surpriseAdList.get(rv.getChildAdapterPosition(childView));
                        if (null != surpriseAd.inMobiNative) {
                            if (surpriseAd.adType == AdType.COMMERCE_AD) {
                                try {
                                    JSONObject content = new JSONObject((String) surpriseAd.inMobiNative.getAdContent());
                                    String landingURL = content.getString("landingURL");
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(landingURL));
                                    startActivity(browserIntent);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                surpriseAd.inMobiNative.reportAdClickAndOpenLandingPage(null);
                            }
                        }
                    }
                } catch (Exception exception) {
                    Log.e("MainActivity", "Happens for Monkey Runner");
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void adAvailable(final List<SurpriseAd> surpriseAdList) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.surpriseAdList.addAll(surpriseAdList);
                myAdapter.notifyDataSetChanged();
            }
        });
        for (SurpriseAd surpriseAd : surpriseAdList) {
            adMap.putIfAbsent(surpriseAd.url, surpriseAd);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean needToRefresh = false;
        for (Map.Entry<String, SurpriseAd> mapEntry : adMap.entrySet()) {
            SurpriseAd surpriseAd = mapEntry.getValue();
            if (System.currentTimeMillis() - surpriseAd.createdAt > validity) {
                needToRefresh = true;
                break;
            }
            if (null != surpriseAd.inMobiNative) {
                surpriseAd.inMobiNative.resume();
            }
        }
        if (needToRefresh) {
            surpriseAdList.clear();
            myAdapter.notifyDataSetChanged();
            adMap.clear();
            Intent appIntent = new Intent(MainActivity.this, AdPreloadActivity.class);
            appIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(appIntent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            adService.fetchAd(MainActivity.this);
            updateAppStatus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        adService.purgeSlottingTask();
        for (Map.Entry<String, SurpriseAd> mapEntry : adMap.entrySet()) {
            SurpriseAd surpriseAd = mapEntry.getValue();
            if (null != surpriseAd.inMobiNative) {
                surpriseAd.inMobiNative.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        surpriseAdList.clear();
        myAdapter.notifyDataSetChanged();
        adMap.clear();
        Clear.clearCache(Picasso.with(this));
        super.onDestroy();
        overridePendingTransition(0, 0);
    }

    private void updateAppStatus() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIF_ID);
        sharedPreferences.edit().putLong(Constants.LAST_APP_OPEN_TIME, System.currentTimeMillis()).apply();
        notificationScheduler.scheduleNotificationService();
    }
}
