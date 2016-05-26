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
import com.ahryk94gmail.mifood.miband.MiBandValue;
import com.ahryk94gmail.mifood.miband.ReadAction;
import com.ahryk94gmail.mifood.miband.RealtimeStepsNotifyAction;
import com.ahryk94gmail.mifood.miband.SetRealtimeStepsNotificationAction;
import com.ahryk94gmail.mifood.miband.SetUserProfileAction;
import com.ahryk94gmail.mifood.model.ActivityData;
import com.ahryk94gmail.mifood.model.UserProfile;
import com.ahryk94gmail.mifood.view.IStepsView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class StepsPresenterImpl implements IStepsPresenter {

    private static final boolean DBG = true;

    private static final long INTRO_ANIM_DURATION = 1000L;
    private static final long PROGRESS_ANIM_DURATION = 2500L;

    private IStepsView mStepsView;
    private BleActionExecutionServiceControlPoint mControlPoint;
    private BroadcastReceiver mRealtimeStepsReceiver;
    private BroadcastReceiver mStepsReceiver;
    private boolean mIsRealtimeStepsUpdateEnabled = false;

    public StepsPresenterImpl(final IStepsView view) {
        this.mStepsView = view;
        this.mStepsView.setGoal(getGoal());
        this.mStepsView.setSteps(0);
        this.mStepsView.setCal(0);
        this.mStepsView.setActivityTime(0);
        this.mStepsView.setDistance(0f);
        this.mControlPoint = BleActionExecutionServiceControlPoint.getInstance(view.getContext());

        this.mRealtimeStepsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!mIsRealtimeStepsUpdateEnabled)
                    return;

                int steps = intent.getIntExtra(BleActionExecutionService.EXTRA_STEPS, 0);
                mStepsView.setSteps(steps);
            }
        };

        this.mStepsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int steps = intent.getIntExtra(BleActionExecutionService.EXTRA_STEPS, 0);

                Calendar today = Calendar.getInstance();

                List<ActivityData> samples = ActivityDbHelper.getInstance(view.getContext())
                        .SelectActivityData(DateTimeUtils.startOfDay(today.getTimeInMillis()), DateTimeUtils.endOfDay(today.getTimeInMillis()));

                mStepsView.setProgress(steps, PROGRESS_ANIM_DURATION, 0L);
                mStepsView.setCal(123);
                mStepsView.setActivityTime(samples.size() * 60);
                mStepsView.setDistance(1.34f);
            }
        };

        LocalBroadcastManager.getInstance(this.mStepsView.getContext())
                .registerReceiver(this.mRealtimeStepsReceiver, new IntentFilter(BleActionExecutionService.ACTION_REALTIME_STEPS));
        LocalBroadcastManager.getInstance(this.mStepsView.getContext())
                .registerReceiver(this.mStepsReceiver, new IntentFilter(BleActionExecutionService.ACTION_STEPS));

        sync();
    }

    @Override
    public void setRealtimeStepsUpdateEnabled(boolean enabled) {
        this.mIsRealtimeStepsUpdateEnabled = enabled;
    }

    @Override
    public void sync() {
        this.mStepsView.showIntro(INTRO_ANIM_DURATION);

        BluetoothDevice bondedDevice = UserPreferences.getInstance().loadBondedDevice(this.mStepsView.getContext());
        UserProfile userProfile = new UserProfile(10000000, UserProfile.GENDER_MALE, 21, 182, 76, "Alex", 0);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(bondedDevice));
        bleActions.add(new SetUserProfileAction(userProfile, bondedDevice.getAddress()));
        bleActions.add(new SetRealtimeStepsNotificationAction());
        bleActions.add(new RealtimeStepsNotifyAction(true));
        bleActions.add(new ReadAction(MiBandValue.STEPS));
        this.mControlPoint.addToQueue(bleActions);
    }

    @Override
    public int getGoal() {
        return 10000;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.mStepsView.getContext()).unregisterReceiver(this.mRealtimeStepsReceiver);
        LocalBroadcastManager.getInstance(this.mStepsView.getContext()).unregisterReceiver(this.mStepsReceiver);
        this.mStepsView = null;
    }
}
