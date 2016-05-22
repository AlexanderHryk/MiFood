package com.ahryk94gmail.mifood.view;

import android.content.Context;

import java.util.Date;

public interface IHeartRateView {

    void startMeasuringAnimation();

    void stopMeasuringAnimation();

    void setHeartRate(int heartRate, long animDuration);

    void setHeartRate(int heartRate);

    void addMeasurementInfo(int heartRate, Date dateTime, boolean heartRateState);

    void showFab();

    void hideFab();

    Context getContext();
}
