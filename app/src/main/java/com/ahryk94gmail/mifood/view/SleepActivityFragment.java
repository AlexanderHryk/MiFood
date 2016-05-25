package com.ahryk94gmail.mifood.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.presenter.ISleepActivityPresenter;
import com.ahryk94gmail.mifood.presenter.SleepActivityPresenterImpl;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class SleepActivityFragment extends Fragment implements ISleepActivityView {

    private static final long EFFECT_SPIRAL_OUT_ANIM_DURATION = 1500L;

    private ISleepActivityPresenter mSleepActivityPresenter;
    private DecoView mDecoView;
    private TextView mTvSleepHours;
    private TextView mTvSleepHoursUnit;
    private TextView mTvSleepMinutes;
    private TextView mTvSleepMinutesUnit;
    private TextView mTvDeepSleepHours;
    private TextView mTvDeepSleepHoursUnit;
    private TextView mTvDeepSleepMinutes;
    private TextView mTvDeepSleepMinutesUnit;
    private TextView mTvLightSleepHours;
    private TextView mTvLightSleepHoursUnit;
    private TextView mTvLightSleepMinutes;
    private TextView mTvLightSleepMinutesUnit;
    private TextView mTvSleepGoal;
    private int mBackgroundSeriesId;
    private int mTotalSleepSeriesId;
    private int mDeepSleepSeriesId;
    private int mLightSleepSeriesId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_activity, container, false);
        this.mDecoView = (DecoView) view.findViewById(R.id.dynamicArcView);

        this.mTvSleepHours = (TextView) view.findViewById(R.id.tv_sleep_hours);
        this.mTvSleepHoursUnit = (TextView) view.findViewById(R.id.tv_sleep_hours_unit);
        this.mTvSleepMinutes = (TextView) view.findViewById(R.id.tv_sleep_minutes);
        this.mTvSleepMinutesUnit = (TextView) view.findViewById(R.id.tv_sleep_minutes_unit);

        this.mTvDeepSleepHours = (TextView) view.findViewById(R.id.tv_deep_sleep_hours);
        this.mTvDeepSleepHoursUnit = (TextView) view.findViewById(R.id.tv_deep_sleep_hours_unit);
        this.mTvDeepSleepMinutes = (TextView) view.findViewById(R.id.tv_deep_sleep_minutes);
        this.mTvDeepSleepMinutesUnit = (TextView) view.findViewById(R.id.tv_deep_sleep_minutes_unit);

        this.mTvLightSleepHours = (TextView) view.findViewById(R.id.tv_light_sleep_hours);
        this.mTvLightSleepHoursUnit = (TextView) view.findViewById(R.id.tv_light_sleep_hours_unit);
        this.mTvLightSleepMinutes = (TextView) view.findViewById(R.id.tv_light_sleep_minutes);
        this.mTvLightSleepMinutesUnit = (TextView) view.findViewById(R.id.tv_light_sleep_minutes_unit);

        this.mTvSleepGoal = (TextView) view.findViewById(R.id.tv_sleep_goal);

        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.nav_sleep));

        SeriesItem backgroundSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorDecoBackground))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(true)
                .build();

        this.mBackgroundSeriesId = this.mDecoView.addSeries(backgroundSeriesItem);

        SeriesItem totalSleepSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorTotalSleep))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(false)
                .build();

        this.mTotalSleepSeriesId = this.mDecoView.addSeries(totalSleepSeriesItem);

        SeriesItem deepSleepSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorDeepSleep))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(false)
                .build();

        this.mDeepSleepSeriesId = this.mDecoView.addSeries(deepSleepSeriesItem);

        SeriesItem lightSleepSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorLightSleep))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(false)
                .build();

        this.mLightSleepSeriesId = this.mDecoView.addSeries(lightSleepSeriesItem);

        this.mSleepActivityPresenter = new SleepActivityPresenterImpl(this);
    }

    public void onDestroy() {
        this.mSleepActivityPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showIntro(long animDuration) {
        this.mDecoView.executeReset();

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.intro_scale);
        anim.setDuration(animDuration);

        getView().findViewById(R.id.deep_sleep_info_container)
                .startAnimation(anim);
        getView().findViewById(R.id.light_sleep_info_container)
                .startAnimation(anim);

        this.mDecoView.addEvent(new DecoEvent.Builder(50f)
                .setIndex(this.mBackgroundSeriesId)
                .setDuration(animDuration)
                .build());
    }

    @Override
    public void setTotalSleepTime(int seconds) {
        int[] time = convertSecondsToHoursMinutes(seconds);

        this.mTvSleepHours.setText(String.valueOf(time[0]));
        this.mTvSleepMinutes.setText(String.valueOf(time[1]));
    }

    @Override
    public void setDeepSleepTime(int seconds) {
        int[] time = convertSecondsToHoursMinutes(seconds);

        this.mTvDeepSleepHours.setText(String.valueOf(time[0]));
        this.mTvDeepSleepMinutes.setText(String.valueOf(time[1]));
    }

    @Override
    public void setLightSleepTime(int seconds) {
        int[] time = convertSecondsToHoursMinutes(seconds);

        this.mTvLightSleepHours.setText(String.valueOf(time[0]));
        this.mTvLightSleepMinutes.setText(String.valueOf(time[1]));
    }

    @Override
    public void setTotalSleepTime(int seconds, long animDuration, long animDelay) {
        final int colorTextPrimary = ContextCompat.getColor(getContext(), R.color.colorTextPrimary);

        this.mTvSleepHours.setTextColor(colorTextPrimary);
        this.mTvSleepHoursUnit.setTextColor(colorTextPrimary);
        this.mTvSleepMinutes.setTextColor(colorTextPrimary);
        this.mTvSleepMinutesUnit.setTextColor(colorTextPrimary);

        setTotalSleepTime(seconds);

        float endPosition = ((float) seconds / this.mSleepActivityPresenter.getGoal()) * 50f;

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mTotalSleepSeriesId)
                .setDuration(EFFECT_SPIRAL_OUT_ANIM_DURATION)
                .setDelay(animDelay)
                .build());

        this.mDecoView.addEvent(new DecoEvent.Builder(endPosition)
                .setIndex(this.mTotalSleepSeriesId)
                .setDuration(animDuration)
                .setDelay(animDelay + EFFECT_SPIRAL_OUT_ANIM_DURATION)
                .build());
    }

    @Override
    public void setDeepSleepTime(int seconds, long animDuration, long animDelay) {
        final int colorTextPrimary = ContextCompat.getColor(getContext(), R.color.colorTextPrimary);

        this.mTvDeepSleepHours.setTextColor(colorTextPrimary);
        this.mTvDeepSleepHoursUnit.setTextColor(colorTextPrimary);
        this.mTvDeepSleepMinutes.setTextColor(colorTextPrimary);
        this.mTvDeepSleepMinutesUnit.setTextColor(colorTextPrimary);

        setDeepSleepTime(seconds);

        float endPosition = ((float) seconds / this.mSleepActivityPresenter.getGoal()) * 50f;

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mDeepSleepSeriesId)
                .setDuration(EFFECT_SPIRAL_OUT_ANIM_DURATION)
                .setDelay(animDelay)
                .build());

        this.mDecoView.addEvent(new DecoEvent.Builder(endPosition)
                .setIndex(this.mDeepSleepSeriesId)
                .setDuration(animDuration)
                .setDelay(animDelay + EFFECT_SPIRAL_OUT_ANIM_DURATION)
                .build());
    }

    @Override
    public void setLightSleepTime(int seconds, long animDuration, long animDelay) {
        final int colorTextPrimary = ContextCompat.getColor(getContext(), R.color.colorTextPrimary);

        this.mTvLightSleepHours.setTextColor(colorTextPrimary);
        this.mTvLightSleepHoursUnit.setTextColor(colorTextPrimary);
        this.mTvLightSleepMinutes.setTextColor(colorTextPrimary);
        this.mTvLightSleepMinutesUnit.setTextColor(colorTextPrimary);

        setLightSleepTime(seconds);

        float endPosition = ((float) seconds / this.mSleepActivityPresenter.getGoal()) * 50f;

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mLightSleepSeriesId)
                .setDuration(EFFECT_SPIRAL_OUT_ANIM_DURATION)
                .setDelay(animDelay)
                .build());

        this.mDecoView.addEvent(new DecoEvent.Builder(endPosition)
                .setIndex(this.mLightSleepSeriesId)
                .setDuration(animDuration)
                .setDelay(animDelay + EFFECT_SPIRAL_OUT_ANIM_DURATION)
                .build());
    }

    @Override
    public void setGoal(int seconds) {
        int[] time = convertSecondsToHoursMinutes(seconds);

        String sleepGoal = getString(R.string.goal) + " "
                + String.valueOf(time[0]) + " " + getString(R.string.hour_unit) + " "
                + String.format("%02d", time[1]) + " " + getString(R.string.min_unit);

        this.mTvSleepGoal.setText(sleepGoal);
    }

    private int[] convertSecondsToHoursMinutes(int seconds) {
        int hours = seconds / (60 * 60);
        int minutes = (seconds % (60 * 60)) / 60;
        int[] result = {hours, minutes};

        return result;
    }

    private int convertHoursMinutesToSeconds(int hours, int minutes) {
        return hours * 60 * 60 + minutes * 60;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync) {
            this.mSleepActivityPresenter.sync();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
