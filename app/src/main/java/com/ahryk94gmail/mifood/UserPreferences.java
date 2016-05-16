package com.ahryk94gmail.mifood;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

public class UserPreferences {

    private static final String FILENAME = "MiFood";
    private static final String BONDED_DEVICE_ADDRESS = "bonded_device_address";

    private static UserPreferences instance;

    public synchronized static UserPreferences getInstance() {
        if (instance == null)
            instance = new UserPreferences();

        return instance;
    }

    public void saveBondedDeviceAddress(Context context, String address) {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
                .edit()
                .putString(BONDED_DEVICE_ADDRESS, address)
                .commit();
    }

    public void removeBondedDeviceAddress(Context context) {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
                .edit()
                .remove(BONDED_DEVICE_ADDRESS)
                .commit();
    }

    public BluetoothDevice loadBondedDevice(Context context) {
        BluetoothDevice bondedDevice = null;
        String address = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
                .getString(BONDED_DEVICE_ADDRESS, "");

        bondedDevice = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter()
                .getRemoteDevice(address);

        return bondedDevice;
    }
}
