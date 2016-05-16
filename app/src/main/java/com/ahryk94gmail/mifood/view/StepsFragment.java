package com.ahryk94gmail.mifood.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.presenter.IStepsPresenter;
import com.ahryk94gmail.mifood.presenter.StepsPresenterImpl;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class StepsFragment extends Fragment implements IStepsView {

    private TextView mTvSteps;
    private IStepsPresenter mStepsPresenter;
    private DecoView mDecoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        this.mTvSteps = (TextView) view.findViewById(R.id.tv_steps);
        this.mDecoView = (DecoView) view.findViewById(R.id.dynamicArcView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.nav_steps_num));

        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, 50, 0)
                .setInitialVisibility(true)
                .build();

        int backIndex = this.mDecoView.addSeries(seriesItem);

        final SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, 50, 0)
                .setInitialVisibility(false)
                .build();

        int series1Index = this.mDecoView.addSeries(seriesItem1);

        this.mDecoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .setDuration(2500)
                .build());

        mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(series1Index)
                .setDuration(2000)
                .setDelay(1250)
                .setEffectRotations(2)
                .build());

        this.mDecoView.addEvent(new DecoEvent.Builder(16.3f)
                .setIndex(series1Index)
                .setDelay(5000)
                .build());

        this.mDecoView.addEvent(new DecoEvent.Builder(30f)
                .setIndex(series1Index)
                .setDelay(10000)
                .build());

        this.mStepsPresenter = new StepsPresenterImpl(this);
    }

    @Override
    public void onDestroy() {
        this.mStepsPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setSteps(int count) {
        this.mTvSteps.setText(count + " шагов");
    }
}
