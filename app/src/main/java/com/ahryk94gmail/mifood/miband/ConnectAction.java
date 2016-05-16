package com.ahryk94gmail.mifood.miband;

import android.bluetooth.BluetoothDevice;

public class ConnectAction implements IBleAction {

    private BluetoothDevice mDevice;

    public ConnectAction(BluetoothDevice device) {
        if (device == null)
            throw new NullPointerException("device");

        this.mDevice = device;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        if (!provider.isConnected()) {
            provider.connect(this.mDevice);
            return true;
        }

        return false;
    }
}
