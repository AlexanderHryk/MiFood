package com.ahryk94gmail.mifood.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.ahryk94gmail.mifood.miband.BleActionExecutionService;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.IBleAction;
import com.ahryk94gmail.mifood.miband.RealtimeStepsNotifyAction;
import com.ahryk94gmail.mifood.miband.SetRealtimeStepsNotificationAction;
import com.ahryk94gmail.mifood.view.IStepsView;

import java.util.LinkedList;
import java.util.List;

public class StepsPresenterImpl implements IStepsPresenter {

    private static final boolean DBG = true;

    private IStepsView mStepsView;
    private BleActionExecutionServiceControlPoint mControlPoint;
    private BroadcastReceiver mRealtimeStepsReceiver;

    public StepsPresenterImpl(IStepsView view) {
        this.mStepsView = view;
        this.mControlPoint = new BleActionExecutionServiceControlPoint(view.getContext());
        this.mControlPoint.bind();

        this.mRealtimeStepsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int steps = intent.getIntExtra(BleActionExecutionService.EXTRA_STEPS, 0);
                mStepsView.setSteps(steps);
            }
        };

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new SetRealtimeStepsNotificationAction());
        bleActions.add(new RealtimeStepsNotifyAction(true));
        this.mControlPoint.addToQueue(bleActions);

        LocalBroadcastManager.getInstance(this.mStepsView.getContext())
                .registerReceiver(this.mRealtimeStepsReceiver, new IntentFilter(BleActionExecutionService.ACTION_REALTIME_STEPS));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.mStepsView.getContext()).unregisterReceiver(this.mRealtimeStepsReceiver);
        this.mStepsView = null;
    }
}
