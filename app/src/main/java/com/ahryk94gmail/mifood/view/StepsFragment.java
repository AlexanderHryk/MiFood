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

import com.ahryk94gmail.mifood.DateTimeUtils;
import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.presenter.IStepsPresenter;
import com.ahryk94gmail.mifood.presenter.StepsPresenterImpl;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class StepsFragment extends Fragment implements IStepsView {

    private IStepsPresenter mStepsPresenter;
    private TextView mTvSteps;
    private TextView mTvStepsUnit;
    private TextView mTvCal;
    private TextView mTvCalUnit;
    private TextView mTvTimeHours;
    private TextView mTvTimeHoursUnit;
    private TextView mTvTimeMinutes;
    private TextView mTvTimeMinutesUnit;
    private TextView mTvDistance;
    private TextView mTvDistanceUnit;
    private TextView mTvStepsGoal;
    private DecoView mDecoView;
    private int mBackgroundSeriesId;
    private int mStepsProgressSeriesId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        this.mTvSteps = (TextView) view.findViewById(R.id.tv_steps);
        this.mTvStepsUnit = (TextView) view.findViewById(R.id.tv_steps_unit);
        this.mTvCal = (TextView) view.findViewById(R.id.tv_cal);
        this.mTvCalUnit = (TextView) view.findViewById(R.id.tv_cal_unit);
        this.mTvTimeHours = (TextView) view.findViewById(R.id.tv_activity_time_hours);
        this.mTvTimeHoursUnit = (TextView) view.findViewById(R.id.tv_activity_time_hours_unit);
        this.mTvTimeMinutes = (TextView) view.findViewById(R.id.tv_activity_time_minutes);
        this.mTvTimeMinutesUnit = (TextView) view.findViewById(R.id.tv_activity_time_minutes_unit);
        this.mTvDistance = (TextView) view.findViewById(R.id.tv_distance);
        this.mTvDistanceUnit = (TextView) view.findViewById(R.id.tv_distance_unit);
        this.mTvStepsGoal = (TextView) view.findViewById(R.id.tv_steps_goal);
        this.mDecoView = (DecoView) view.findViewById(R.id.dynamicArcView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.nav_steps_num));

        SeriesItem backgroundSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorDecoBackground))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(true)
                .build();

        this.mBackgroundSeriesId = this.mDecoView.addSeries(backgroundSeriesItem);

        SeriesItem stepsProgressSeriesItem = new SeriesItem.Builder(ContextCompat.getColor(getContext(), R.color.colorDecoSeries1))
                .setRange(0f, 50f, 0f)
                .setInitialVisibility(true)
                .build();

        stepsProgressSeriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                setSteps((int) (mStepsPresenter.getGoal() * currentPosition / 50f));
                if (percentComplete == 1f && currentPosition != 0f)
                    mStepsPresenter.setRealtimeStepsUpdateEnabled(true);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        this.mStepsPresenter = new StepsPresenterImpl(this);

        this.mStepsProgressSeriesId = this.mDecoView.addSeries(stepsProgressSeriesItem);
    }

    @Override
    public void onDestroy() {
        this.mStepsPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setSteps(int steps) {
        this.mTvSteps.setText(String.valueOf(String.format("%,d", steps)));
    }

    @Override
    public void setGoal(int steps) {
        this.mTvStepsGoal.setText(getString(R.string.goal) + " " + String.format("%,d", steps)
                + " " + getString(R.string.steps));
    }

    @Override
    public void setCal(int cal) {
        this.mTvCal.setText(String.format("%,d", cal));
    }

    @Override
    public void setActivityTime(int seconds) {
        int[] time = DateTimeUtils.convertSecondsToHoursMinutes(seconds);

        this.mTvTimeHours.setText(String.valueOf(time[0]));
        this.mTvTimeMinutes.setText(String.valueOf(time[1]));
    }

    @Override
    public void setDistance(float distance) {
        this.mTvDistance.setText(String.format("%.2f", distance));
    }

    @Override
    public void showIntro(long animDuration) {
        this.mDecoView.executeReset();

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.intro_scale);
        anim.setDuration(animDuration);

        getView().findViewById(R.id.cal_burned_info_container)
                .startAnimation(anim);
        getView().findViewById(R.id.time_info_container)
                .startAnimation(anim);
        getView().findViewById(R.id.distance_info_container)
                .startAnimation(anim);

        this.mDecoView.addEvent(new DecoEvent.Builder(50f)
                .setIndex(this.mBackgroundSeriesId)
                .setDuration(animDuration)
                .build());
    }

    @Override
    public void setProgress(int steps, long animDuration, long animDelay) {
        this.mStepsPresenter.setRealtimeStepsUpdateEnabled(false);

        final int colorTextPrimary = ContextCompat.getColor(getContext(), R.color.colorTextPrimary);

        this.mTvSteps.setTextColor(colorTextPrimary);
        this.mTvStepsUnit.setTextColor(colorTextPrimary);
        this.mTvCal.setTextColor(colorTextPrimary);
        this.mTvCalUnit.setTextColor(colorTextPrimary);
        this.mTvTimeHours.setTextColor(colorTextPrimary);
        this.mTvTimeHoursUnit.setTextColor(colorTextPrimary);
        this.mTvTimeMinutes.setTextColor(colorTextPrimary);
        this.mTvTimeMinutesUnit.setTextColor(colorTextPrimary);
        this.mTvDistance.setTextColor(colorTextPrimary);
        this.mTvDistanceUnit.setTextColor(colorTextPrimary);

        float endPosition = (float) steps / this.mStepsPresenter.getGoal() * 50f;

        this.mDecoView.addEvent(new DecoEvent.Builder(endPosition)
                .setIndex(this.mStepsProgressSeriesId)
                .setDuration(animDuration)
                .setDelay(animDelay)
                .build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sync) {
            this.mStepsPresenter.sync();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
