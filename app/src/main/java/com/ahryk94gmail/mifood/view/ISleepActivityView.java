package com.ahryk94gmail.mifood.view;

import android.content.Context;

public interface ISleepActivityView {

    void showIntro(long animDuration);

    void setTotalSleepTime(int seconds);

    void setGoal(int seconds);

    void setDeepSleepTime(int seconds);

    void setLightSleepTime(int seconds);

    void setTotalSleepTime(int seconds, long animDuration, long animDelay);

    void setDeepSleepTime(int seconds, long animDuration, long animDelay);

    void setLightSleepTime(int seconds, long animDuration, long animDelay);

    Context getContext();
}
