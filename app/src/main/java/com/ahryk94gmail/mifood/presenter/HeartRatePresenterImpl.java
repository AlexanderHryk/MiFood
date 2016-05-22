package com.ahryk94gmail.mifood.presenter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.ahryk94gmail.mifood.UserPreferences;
import com.ahryk94gmail.mifood.miband.BleActionExecutionService;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.ConnectAction;
import com.ahryk94gmail.mifood.miband.IBleAction;
import com.ahryk94gmail.mifood.miband.SetHeartRateNotificationAction;
import com.ahryk94gmail.mifood.miband.SetUserProfileAction;
import com.ahryk94gmail.mifood.miband.StartHeartRateMeasuringAction;
import com.ahryk94gmail.mifood.model.UserProfile;
import com.ahryk94gmail.mifood.view.IHeartRateView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class HeartRatePresenterImpl implements IHeartRatePresenter {

    private static final boolean DBG = true;

    private IHeartRateView mHeartRateView;
    private BleActionExecutionServiceControlPoint mControlPoint;
    private BroadcastReceiver mHeartRateReceiver;

    public HeartRatePresenterImpl(IHeartRateView view) {
        this.mHeartRateView = view;
        this.mHeartRateView.setHeartRate(0);
        this.mControlPoint = new BleActionExecutionServiceControlPoint(view.getContext());
        this.mControlPoint.bind();

        this.mHeartRateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int heartRate = intent.getIntExtra(BleActionExecutionService.EXTRA_HEART_RATE, 0);
                mHeartRateView.stopMeasuringAnimation();
                mHeartRateView.setHeartRate(heartRate, 1000L);
                mHeartRateView.addMeasurementInfo(heartRate, Calendar.getInstance().getTime(), heartRate < 90);
                mHeartRateView.showFab();
            }
        };

        LocalBroadcastManager.getInstance(this.mHeartRateView.getContext())
                .registerReceiver(this.mHeartRateReceiver, new IntentFilter(BleActionExecutionService.ACTION_HEART_RATE));
    }

    @Override
    public void startHeartRateMeasuring() {
        mHeartRateView.hideFab();
        mHeartRateView.startMeasuringAnimation();
        BluetoothDevice bondedDevice = UserPreferences.getInstance().loadBondedDevice(this.mHeartRateView.getContext());
        UserProfile userProfile = new UserProfile(10000000, 1, 21, 182, 76, "Alex", 0);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(bondedDevice));
        bleActions.add(new SetUserProfileAction(userProfile, bondedDevice.getAddress()));
        bleActions.add(new SetHeartRateNotificationAction());
        bleActions.add(new StartHeartRateMeasuringAction());
        this.mControlPoint.addToQueue(bleActions);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.mHeartRateView.getContext()).unregisterReceiver(this.mHeartRateReceiver);
        this.mHeartRateView = null;
    }
}
