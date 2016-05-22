package com.ahryk94gmail.mifood.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.presenter.HeartRatePresenterImpl;
import com.ahryk94gmail.mifood.presenter.IHeartRatePresenter;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.skyfishjy.library.RippleBackground;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HeartRateFragment extends Fragment implements IHeartRateView, View.OnClickListener {

    private static final String ATTRIBUTE_HEART_RATE = "heart_rate";
    private static final String ATTRIBUTE_DATE_TIME = "date_time";
    private static final String ATTRIBUTE_HEART_RATE_STATE = "heart_rate_state";

    private DecoView mDecoView;
    private TextView mTvHeartRate;
    private TextView mTvHeartRateUnit;
    private int mHeartRateMeasuringProgressSeriesId;
    private RippleBackground mRippleBackground;
    private int mHeartRate;
    private ListView mLvHistory;
    private ArrayList<Map<String, Object>> mData;
    private SimpleAdapter mAdapter;
    private FloatingActionButton mFabStartHrMeasuring;
    private IHeartRatePresenter mHeartRatePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);
        this.mRippleBackground = (RippleBackground) view.findViewById(R.id.content);
        this.mDecoView = (DecoView) view.findViewById(R.id.dynamicArcView);
        this.mTvHeartRate = (TextView) view.findViewById(R.id.tv_heart_rate);
        this.mTvHeartRateUnit = (TextView) view.findViewById(R.id.tv_heart_rate_unit);
        this.mLvHistory = (ListView) view.findViewById(R.id.lv_history);
        this.mFabStartHrMeasuring = (FloatingActionButton) view.findViewById(R.id.fab_start_hr_measuring);
        this.mFabStartHrMeasuring.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.nav_heart_rate));

        SeriesItem heartRateMeasuringProgressSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorRed))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(true)
                .build();

        this.mHeartRateMeasuringProgressSeriesId = this.mDecoView.addSeries(heartRateMeasuringProgressSeriesItem);

        heartRateMeasuringProgressSeriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                setHeartRate((int) (mHeartRate * percentComplete));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        this.mData = new ArrayList<>();
        String[] from = {ATTRIBUTE_HEART_RATE, ATTRIBUTE_DATE_TIME, ATTRIBUTE_HEART_RATE_STATE, ATTRIBUTE_HEART_RATE_STATE};
        int[] to = {R.id.tv_heart_rate, R.id.tv_date_time, R.id.tv_heart_rate_state, R.id.iv_heart_rate_state_icon};
        this.mAdapter = new SimpleAdapter(getContext(), this.mData, R.layout.heartrate_measurement_info_item, from, to);
        this.mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                int id = view.getId();

                if (id == R.id.tv_heart_rate_state) {
                    if ((boolean) data) {
                        ((TextView) view).setText(getString(R.string.heart_rate_good));
                    } else {
                        ((TextView) view).setText(getString(R.string.heart_rate_bad));
                    }
                    return true;
                } else if (id == R.id.iv_heart_rate_state_icon) {
                    if ((boolean) data) {
                        ((ImageView) view).setImageResource(R.drawable.ic_heart_rate_good);
                    } else {
                        ((ImageView) view).setImageResource(R.drawable.ic_heart_rate_bad);
                    }
                    return true;
                }
                return false;
            }
        });
        this.mLvHistory.setAdapter(this.mAdapter);

        this.mHeartRatePresenter = new HeartRatePresenterImpl(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.fab_start_hr_measuring) {
            this.mHeartRatePresenter.startHeartRateMeasuring();
        }
    }


    @Override
    public void startMeasuringAnimation() {
        this.mDecoView.executeReset();
        final int colorTextPrimaryTransparent = ContextCompat.getColor(getContext(), R.color.colorTextPrimaryTransparent);
        this.mTvHeartRate.setTextColor(colorTextPrimaryTransparent);
        this.mTvHeartRateUnit.setTextColor(colorTextPrimaryTransparent);
        this.mRippleBackground.startRippleAnimation();
    }

    @Override
    public void stopMeasuringAnimation() {
        this.mRippleBackground.stopRippleAnimation();
    }

    @Override
    public void setHeartRate(int heartRate, long animDuration) {
        this.mHeartRate = heartRate;

        final int colorTextPrimary = ContextCompat.getColor(getContext(), R.color.colorTextPrimary);
        this.mTvHeartRate.setTextColor(colorTextPrimary);
        this.mTvHeartRateUnit.setTextColor(colorTextPrimary);

        this.mDecoView.addEvent(new DecoEvent.Builder(50f)
                .setIndex(this.mHeartRateMeasuringProgressSeriesId)
                .setDuration(animDuration)
                .build());
    }

    @Override
    public void setHeartRate(int heartRate) {
        this.mTvHeartRate.setText(String.valueOf(heartRate));
    }

    @Override
    public void addMeasurementInfo(int heartRate, Date dateTime, boolean heartRateState) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\tHH:mm:ss");

        Map<String, Object> item = new HashMap<>();
        item.put(ATTRIBUTE_HEART_RATE, String.valueOf(heartRate));
        item.put(ATTRIBUTE_DATE_TIME, sdf.format(dateTime));
        item.put(ATTRIBUTE_HEART_RATE_STATE, heartRateState);
        this.mData.add(item);
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showFab() {
        this.mFabStartHrMeasuring.setEnabled(true);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_show);
        this.mFabStartHrMeasuring.startAnimation(anim);
        anim.setFillAfter(true);
    }

    @Override
    public void hideFab() {
        this.mFabStartHrMeasuring.setEnabled(false);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_hide);
        this.mFabStartHrMeasuring.startAnimation(anim);
        anim.setFillAfter(true);
    }
}
