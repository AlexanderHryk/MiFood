package com.ahryk94gmail.mifood.view;

import android.content.Context;

public interface IStepsView {

    void showIntro(long animDuration);

    void setProgress(int steps, long animDuration, long animDelay);

    void setSteps(int steps);

    void setGoal(int steps);

    void setCal(int cal);

    void setActivityTime(int seconds);

    void setDistance(float distance);

    Context getContext();
}
