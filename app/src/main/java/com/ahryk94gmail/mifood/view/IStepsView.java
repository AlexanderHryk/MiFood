package com.ahryk94gmail.mifood.view;

import android.content.Context;

public interface IStepsView {

    void setSteps(int steps);

    void setGoal(int goal);

    void setCal(int cal);

    void setTime(int min);

    void setDistance(float distance);

    void showInto(long animDuration);

    void setProgress(int steps, long animDuration, long animDelay);

    Context getContext();
}
