package com.ahryk94gmail.mifood.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.ahryk94gmail.mifood.UserPreferences;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.ConnectAction;
import com.ahryk94gmail.mifood.miband.IBleAction;
import com.ahryk94gmail.mifood.miband.StartVibrationAction;
import com.ahryk94gmail.mifood.miband.VibrationMode;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CallReceiver extends PhoneCallReceiver {
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        BluetoothDevice bondedDevice = UserPreferences.getInstance().loadBondedDevice(ctx);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(bondedDevice));
        bleActions.add(new StartVibrationAction(VibrationMode.VIBRATION_10_TIMES_WITH_LED));
        BleActionExecutionServiceControlPoint.getInstance(ctx).addToQueue(bleActions);
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }
}
