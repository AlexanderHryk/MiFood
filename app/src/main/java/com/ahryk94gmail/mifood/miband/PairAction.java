package com.ahryk94gmail.mifood.miband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class PairAction extends WriteAction {

    private static final long DELAY = 3000L;

    private Context mContext;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mAdapter;

    public PairAction(Context context, BluetoothDevice device) {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_PAIR, Constants.protocol.PAIR);

        if (device == null)
            throw new NullPointerException("device");

        this.mContext = context;
        this.mDevice = device;
        this.mAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        Intent intent = new Intent(BleActionExecutionService.ACTION_PAIRED)
                .putExtra(BleActionExecutionService.EXTRA_DEVICE_ADDRESS, this.mDevice.getAddress());

        if (super.execute(provider)) {
            Thread.sleep(DELAY);

            if (this.mAdapter.getBondedDevices().contains(this.mDevice)) {
                intent.putExtra(BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE, BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE_SUCCESS);
                LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(intent);
                return false;
            }
        }

        intent.putExtra(BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE, BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE_FAILURE);
        LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(intent);

        return false;
    }
}
