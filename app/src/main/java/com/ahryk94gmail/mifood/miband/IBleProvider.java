package com.ahryk94gmail.mifood.miband;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public interface IBleProvider {

    void connect(BluetoothDevice device);

    void disconnect();

    boolean isConnected();

    void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value);

    void readCharacteristic(UUID serviceUUID, UUID characteristicUUID);

    void setCharacteristicNotification(UUID serviceUUID, UUID characteristicUUID);
}
