package com.ahryk94gmail.mifood.presenter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.ahryk94gmail.mifood.DateTimeUtils;
import com.ahryk94gmail.mifood.UserPreferences;
import com.ahryk94gmail.mifood.db.ActivityDbHelper;
import com.ahryk94gmail.mifood.miband.BleActionExecutionService;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.ConnectAction;
import com.ahryk94gmail.mifood.miband.IBleAction;
import com.ahryk94gmail.mifood.miband.SetActivityNotificationAction;
import com.ahryk94gmail.mifood.miband.SetUserProfileAction;
import com.ahryk94gmail.mifood.miband.StartSyncAction;
import com.ahryk94gmail.mifood.model.ActivityAmount;
import com.ahryk94gmail.mifood.model.ActivityAmounts;
import com.ahryk94gmail.mifood.model.ActivityAnalysis;
import com.ahryk94gmail.mifood.model.ActivityData;
import com.ahryk94gmail.mifood.model.UserProfile;
import com.ahryk94gmail.mifood.view.ISleepActivityView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class SleepActivityPresenterImpl implements ISleepActivityPresenter {

    private static final boolean DBG = true;
    private static final long INTRO_ANIM_DURATION = 1000L;
    private static final long PROGRESS_ANIM_DURATION = 1000L;

    private ISleepActivityView mSleepActivityView;
    private BleActionExecutionServiceControlPoint mControlPoint;
    private BroadcastReceiver mSyncCompletedReceiver;


    public SleepActivityPresenterImpl(ISleepActivityView view) {
        this.mSleepActivityView = view;
        this.mControlPoint = BleActionExecutionServiceControlPoint.getInstance(view.getContext());

        this.mSyncCompletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Calendar today = Calendar.getInstance();

                List<ActivityData> samples = ActivityDbHelper.getInstance(context)
                        .SelectActivityData(DateTimeUtils.startOfDay(today.getTimeInMillis()), DateTimeUtils.endOfDay(today.getTimeInMillis()));
                ActivityAnalysis analysis = new ActivityAnalysis();
                ActivityAmounts amounts = analysis.calculateActivityAmounts(samples);

                ActivityAmount deepSleepAmount = new ActivityAmount(ActivityData.TYPE_DEEP_SLEEP);
                ActivityAmount lightSleepAmount = new ActivityAmount(ActivityData.TYPE_LIGHT_SLEEP);

                for (ActivityAmount amount : amounts.getAmounts()) {
                    if (amount.getActivityType() == ActivityData.TYPE_DEEP_SLEEP) {
                        deepSleepAmount = amount;
                    } else if (amount.getActivityType() == ActivityData.TYPE_LIGHT_SLEEP) {
                        lightSleepAmount = amount;
                    }
                }

                mSleepActivityView.setTotalSleepTime((int) (deepSleepAmount.getTotalSeconds() + lightSleepAmount.getTotalSeconds()),
                        PROGRESS_ANIM_DURATION, INTRO_ANIM_DURATION);

                long animDelayFirst = INTRO_ANIM_DURATION + PROGRESS_ANIM_DURATION;
                long animDelaySecond = INTRO_ANIM_DURATION + 2 * PROGRESS_ANIM_DURATION;

                if (deepSleepAmount.getTotalSeconds() > lightSleepAmount.getTotalSeconds()) {
                    mSleepActivityView.setDeepSleepTime((int) deepSleepAmount.getTotalSeconds(), PROGRESS_ANIM_DURATION, animDelayFirst);
                    mSleepActivityView.setLightSleepTime((int) lightSleepAmount.getTotalSeconds(), PROGRESS_ANIM_DURATION, animDelaySecond);
                } else {
                    mSleepActivityView.setLightSleepTime((int) lightSleepAmount.getTotalSeconds(), PROGRESS_ANIM_DURATION, animDelayFirst);
                    mSleepActivityView.setDeepSleepTime((int) deepSleepAmount.getTotalSeconds(), PROGRESS_ANIM_DURATION, animDelaySecond);
                }
            }
        };

        this.mSleepActivityView.setGoal(getGoal());
        this.mSleepActivityView.setTotalSleepTime(0);
        this.mSleepActivityView.setDeepSleepTime(0);
        this.mSleepActivityView.setLightSleepTime(0);

        LocalBroadcastManager.getInstance(this.mSleepActivityView.getContext())
                .registerReceiver(this.mSyncCompletedReceiver, new IntentFilter(BleActionExecutionService.ACTION_SYNC_COMPLETED));

        sync();
    }

    @Override
    public int getGoal() {
        return 8 * 60 * 60;
    }

    @Override
    public void sync() {
        this.mSleepActivityView.showIntro(INTRO_ANIM_DURATION);

        BluetoothDevice bondedDevice = UserPreferences.getInstance().loadBondedDevice(this.mSleepActivityView.getContext());
        UserProfile userProfile = new UserProfile(10000000, UserProfile.GENDER_MALE, 21, 182, 76, "Alex", 0);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(bondedDevice));
        bleActions.add(new SetUserProfileAction(userProfile, bondedDevice.getAddress()));
        bleActions.add(new SetActivityNotificationAction());
        bleActions.add(new StartSyncAction());
        this.mControlPoint.addToQueue(bleActions);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.mSleepActivityView.getContext()).unregisterReceiver(this.mSyncCompletedReceiver);
        this.mSleepActivityView = null;
    }
}
