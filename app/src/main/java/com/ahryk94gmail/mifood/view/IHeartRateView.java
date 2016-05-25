package com.ahryk94gmail.mifood.view;

import android.content.Context;

import com.ahryk94gmail.mifood.model.HrMeasurementInfo;

import java.util.List;

public interface IHeartRateView {

    void startMeasuringAnimation();

    void stopMeasuringAnimation();

    void setHeartRate(int heartRate, long animDuration);

    void setHeartRate(int heartRate);

    void addMeasurementInfo(HrMeasurementInfo info);

    void addMeasurementInfo(List<HrMeasurementInfo> info);

    void showFab();

    void hideFab();

    Context getContext();
}
