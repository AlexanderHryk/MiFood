package com.ahryk94gmail.mifood.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ahryk94gmail.mifood.UserPreferences;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.ConnectAction;
import com.ahryk94gmail.mifood.miband.IBleAction;
import com.ahryk94gmail.mifood.miband.StartVibrationAction;
import com.ahryk94gmail.mifood.miband.VibrationMode;

import java.util.LinkedList;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BluetoothDevice bondedDevice = UserPreferences.getInstance().loadBondedDevice(context);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(bondedDevice));
        bleActions.add(new StartVibrationAction(VibrationMode.VIBRATION_WITH_LED));
        BleActionExecutionServiceControlPoint.getInstance(context).addToQueue(bleActions);
    }
}
