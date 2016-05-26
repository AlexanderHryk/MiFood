package com.ahryk94gmail.mifood.presenter;

public interface IStepsPresenter {

    void setRealtimeStepsUpdateEnabled(boolean enabled);

    int getGoal();

    void sync();

    void onDestroy();
}
