package com.ahryk94gmail.mifood.presenter;

public interface IStepsPresenter {

    void setRealtimeStepsUpdateEnabled(boolean enabled);

    void sync();

    int getGoal();

    void onDestroy();
}
