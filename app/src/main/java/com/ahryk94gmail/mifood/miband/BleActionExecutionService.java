package com.ahryk94gmail.mifood.miband;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.ahryk94gmail.mifood.Debug;
import com.ahryk94gmail.mifood.db.ActivityDbHelper;
import com.ahryk94gmail.mifood.model.ActivityData;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BleActionExecutionService extends Service {

    public static final String ACTION_CONNECTION_STATE_CHANGED = "com.ahryk94gmail.mifood.miband.ACTION_CONNECTION_STATE_CHANGED";
    public static final String ACTION_PAIRED = "com.ahryk94gmail.mifood.miband.ACTION_PAIRED";
    public static final String ACTION_REALTIME_STEPS = "com.ahryk94gmail.mifood.miband.ACTION_REALTIME_STEPS";
    public static final String ACTION_STEPS = "com.ahryk94gmail.mifood.miband.ACTION_STEPS";
    public static final String ACTION_HEART_RATE = "com.ahryk94gmail.mifood.miband.ACTION_HEART_RATE";
    public static final String ACTION_SYNC_COMPLETED = "com.ahryk94gmail.mifood.miband.ACTION_SYNC_COMPLETED";

    public static final String EXTRA_CONNECTION_STATE = "com.ahryk94gmail.mifood.miband.EXTRA_CONNECTION_STATE";
    public static final String EXTRA_PAIRING_RESULT_CODE = "com.ahryk94gmail.mifood.miband.EXTRA_PAIRING_RESULT_CODE";
    public static final String EXTRA_DEVICE_ADDRESS = "com.ahryk94gmail.mifood.miband.EXTRA_DEVICE_ADDRESS";
    public static final String EXTRA_STEPS = "com.ahryk94gmail.mifood.miband.EXTRA_STEPS_NUM";
    public static final String EXTRA_HEART_RATE = "com.ahryk94gmail.mifood.miband.EXTRA_HEART_RATE";

    public static final int EXTRA_CONNECTION_STATE_CONNECTED = 0x100;
    public static final int EXTRA_CONNECTION_STATE_DISCONNECTED = 0x101;
    public static final int EXTRA_PAIRING_RESULT_CODE_SUCCESS = 0x200;
    public static final int EXTRA_PAIRING_RESULT_CODE_FAILURE = 0x201;

    private static final boolean DBG = true;
    private static final Object monitor = new Object();

    private MiBandProvider mProvider;
    private ExecutorService mExecutor;
    private BleActionExecutionServiceBinder mBinder;
    private ActivityDbHelper mActivityDbHelper;

    class MiBandProvider extends BluetoothGattCallback implements IBleProvider {

        private BluetoothGatt mGatt;
        private boolean mIsServicesDiscovered = false;

        @Override
        public void connect(BluetoothDevice device) {
            this.mGatt = device.connectGatt(BleActionExecutionService.this, false, this);
        }

        @Override
        public void disconnect() {
            if (this.mGatt == null) return;

            this.mGatt.disconnect();
        }

        @Override
        public boolean isConnected() {
            return this.mIsServicesDiscovered;
        }

        private void close() {
            if (this.mGatt == null) return;

            this.mGatt.close();
            this.mGatt = null;
            this.mIsServicesDiscovered = false;
        }

        @Override
        public void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
            if (this.mGatt == null || !this.mIsServicesDiscovered) {
                Debug.WriteLog(DBG, "writeCharacteristic: device is not connected or services are not discovered");
                return;
            }

            BluetoothGattCharacteristic chara = this.mGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            chara.setValue(value);

            if (!this.mGatt.writeCharacteristic(chara)) {
                Debug.WriteLog(DBG, "writeCharacteristic: false");
            }
        }

        @Override
        public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
            if (this.mGatt == null || !this.mIsServicesDiscovered) {
                Debug.WriteLog(DBG, "readCharacteristic: device is not connected or services are not discovered");
                return;
            }

            BluetoothGattCharacteristic chara = this.mGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);

            if (!this.mGatt.readCharacteristic(chara)) {
                Debug.WriteLog(DBG, "readCharacteristic: false");
            }
        }

        @Override
        public void setCharacteristicNotification(UUID serviceUUID, UUID characteristicUUID) {
            if (this.mGatt == null || !this.mIsServicesDiscovered) {
                Debug.WriteLog(DBG, "setCharacteristicNotification: device is not connected or services are not discovered");
                return;
            }

            BluetoothGattCharacteristic chara = this.mGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);

            this.mGatt.setCharacteristicNotification(chara, true);
            BluetoothGattDescriptor descriptor = chara.getDescriptor(Constants.descriptors.CLIENT_CHARACTERISTIC_CONFIG);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (!this.mGatt.writeDescriptor(descriptor)) {
                Debug.WriteLog(DBG, "writeDescriptor: false");
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                this.mGatt = gatt;

                if (!gatt.discoverServices()) {
                    Debug.WriteLog(DBG, "services discovery process has not been started");
                    close();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Debug.WriteLog(DBG, "disconnected: " + gatt.getDevice().getAddress());
                Intent intent = new Intent(ACTION_CONNECTION_STATE_CHANGED)
                        .putExtra(EXTRA_CONNECTION_STATE, EXTRA_CONNECTION_STATE_DISCONNECTED);
                LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
                close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                this.mGatt = gatt;
                this.mIsServicesDiscovered = true;

                Debug.WriteLog(DBG, "connected: " + gatt.getDevice().getAddress());
                Intent intent = new Intent(ACTION_CONNECTION_STATE_CHANGED)
                        .putExtra(EXTRA_CONNECTION_STATE, EXTRA_CONNECTION_STATE_CONNECTED);
                LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
            } else {
                Debug.WriteLog(DBG, "onServicesDiscovered, status: " + status);
                close();
            }

            yield();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Debug.WriteLog(DBG, "onCharacteristicRead, status: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Intent intent = new Intent();
                byte[] data = characteristic.getValue();

                if (characteristic.getUuid().equals(Constants.characteristics.UUID_CHAR_REALTIME_STEPS)) {
                    int steps = handleStepsNotification(data);
                    intent.setAction(ACTION_STEPS).putExtra(EXTRA_STEPS, steps);
                }

                LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
            } else {

            }

            yield();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Debug.WriteLog(DBG, "onCharacteristicWrite, status: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {

            }

            yield();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Debug.WriteLog(DBG, "onDescriptorWrite, status: " + status);
            yield();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            Intent intent = new Intent();
            byte[] data = characteristic.getValue();

            if (characteristic.getUuid().equals(Constants.characteristics.UUID_CHAR_REALTIME_STEPS)) {
                int steps = handleStepsNotification(data);
                intent.setAction(ACTION_REALTIME_STEPS).putExtra(EXTRA_STEPS, steps);
            } else if (characteristic.getUuid().equals(Constants.characteristics.UUID_CHAR_HEART_RATE)) {
                int heartRate = handleHeartRateNotification(data);
                intent.setAction(ACTION_HEART_RATE).putExtra(EXTRA_HEART_RATE, heartRate);
            } else if (characteristic.getUuid().equals(Constants.characteristics.UUID_CHAR_ACTIVITY)) {
                handleActivityDataNotification(data);
            }

            LocalBroadcastManager.getInstance(BleActionExecutionService.this).sendBroadcast(intent);
        }
    }

    class BleActionRunner implements Runnable {

        private IBleAction mBleAction;

        public BleActionRunner(IBleAction bleAction) {
            this.mBleAction = bleAction;
        }

        @Override
        public void run() {
            synchronized (monitor) {
                Debug.WriteLog(DBG, "starts: " + this.mBleAction.getClass().getSimpleName());

                try {
                    if (this.mBleAction.execute(mProvider)) {
                        monitor.wait();
                    }
                } catch (InterruptedException e) {
                    Debug.WriteLog(DBG, e.getMessage());
                }

                Debug.WriteLog(DBG, "finished: " + this.mBleAction.getClass().getSimpleName());
            }
        }
    }

    class BleActionExecutionServiceBinder extends Binder implements IBleActionExecutionServiceControlPoint {

        @Override
        public boolean addToQueue(IBleAction bleAction) {
            mExecutor.execute(new BleActionRunner(bleAction));

            return true;
        }

        @Override
        public boolean addToQueue(List<IBleAction> bleActions) {
            for (IBleAction bleAction : bleActions) {
                addToQueue(bleAction);
            }

            return true;
        }
    }

    private static void yield() {
        synchronized (monitor) {
            Debug.WriteLog(DBG, "yield");
            monitor.notify();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Debug.WriteLog(DBG, "BleActionExecutionService: onCreate");

        this.mProvider = new MiBandProvider();
        this.mExecutor = Executors.newFixedThreadPool(1);
        this.mBinder = new BleActionExecutionServiceBinder();
        this.mActivityDbHelper = ActivityDbHelper.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Debug.WriteLog(DBG, "BleActionService: onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Debug.WriteLog(DBG, "BleActionExecutionService: onDestroy");
        this.mProvider.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Debug.WriteLog(DBG, "BleActionExecutionService: onBind");
        return this.mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Debug.WriteLog(DBG, "BleActionExecutionService: onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Debug.WriteLog(DBG, "BleActionExecutionService: onRebind");
    }

    private int handleStepsNotification(byte[] data) {
        return data[3] << 24 | (data[2] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | (data[0] & 0xFF);
    }

    private int handleHeartRateNotification(byte[] data) {
        int heartRate = -1;

        if (data.length == 2 && data[0] == 6) {
            heartRate = data[1] & 0xFF;
        }
        return heartRate;
    }

    private static final int activityDataHolderSize = 3 * 60 * 4;

    private static class ActivityStruct {
        public byte[] activityDataHolder = new byte[activityDataHolderSize];
        public int activityDataHolderProgress = 0;
        public int activityDataRemainingBytes = 0;
        public int activityDataUntilNextHeader = 0;
        public GregorianCalendar activityDataTimestampProgress = null;
        public GregorianCalendar activityDataTimestampToAck = null;
    }

    private ActivityStruct activityStruct;

    private void handleActivityDataNotification(byte[] data) {
        boolean firstChunk = activityStruct == null;
        if (firstChunk) {
            activityStruct = new ActivityStruct();
        }

        if (data.length == 11) {
            int dataType = data[0];

            GregorianCalendar timestamp = parseTimestamp(data, 1);

            int totalDataToRead = (data[7] & 0xFF) | ((data[8] & 0xFF) << 8);
            totalDataToRead *= (dataType == 1) ? 3 : 1;

            int dataUntilNextHeader = (data[9] & 0xFF) | ((data[10] & 0xFF) << 8);
            dataUntilNextHeader *= (dataType == 1) ? 3 : 1;

            activityStruct.activityDataRemainingBytes = activityStruct.activityDataUntilNextHeader = dataUntilNextHeader;
            activityStruct.activityDataTimestampToAck = (GregorianCalendar) timestamp.clone();
            activityStruct.activityDataTimestampProgress = timestamp;
        } else {
            bufferActivityData(data);
        }

        if (activityStruct.activityDataRemainingBytes == 0) {
            sendAckDataTransfer(activityStruct.activityDataTimestampToAck, activityStruct.activityDataUntilNextHeader);
        }
    }

    private void bufferActivityData(byte[] value) {
        if (activityStruct.activityDataRemainingBytes >= value.length) {
            if (value.length == 20 || value.length == activityStruct.activityDataRemainingBytes) {
                System.arraycopy(value, 0, activityStruct.activityDataHolder, activityStruct.activityDataHolderProgress, value.length);
                activityStruct.activityDataHolderProgress += value.length;
                activityStruct.activityDataRemainingBytes -= value.length;

                if (this.activityDataHolderSize == activityStruct.activityDataHolderProgress) {
                    flushActivityDataHolder();
                }
            }
        }
    }

    private void flushActivityDataHolder() {
        if (activityStruct == null) {
            return;
        }

        byte type, intensity, steps;

        for (int i = 0; i < activityStruct.activityDataHolderProgress; i += 3) {
            type = activityStruct.activityDataHolder[i];
            intensity = activityStruct.activityDataHolder[i + 1];
            steps = activityStruct.activityDataHolder[i + 2];

            this.mActivityDbHelper.InsertActivityData(new ActivityData(
                    (int) (activityStruct.activityDataTimestampProgress.getTimeInMillis() / 1000),
                    ActivityData.PROVIDER_MIBAND,
                    intensity,
                    steps,
                    type
            ));

            activityStruct.activityDataTimestampProgress.add(Calendar.MINUTE, 1);
        }

        activityStruct.activityDataHolderProgress = 0;
    }

    private void sendAckDataTransfer(Calendar time, int bytesTransferred) {
        byte[] ack = new byte[]{
                Constants.protocol.COMMAND_CONFIRM_ACTIVITY_DATA_TRANSFER_COMPLETE,
                (byte) (time.get(Calendar.YEAR) - 2000),
                (byte) time.get(Calendar.MONTH),
                (byte) time.get(Calendar.DATE),
                (byte) time.get(Calendar.HOUR_OF_DAY),
                (byte) time.get(Calendar.MINUTE),
                (byte) time.get(Calendar.SECOND),
                (byte) (bytesTransferred & 0xFF),
                (byte) (0xFF & (bytesTransferred >> 8))
        };

        BleActionExecutionServiceControlPoint.getInstance(this).addToQueue(new SendAckAction(ack));

        flushActivityDataHolder();

        if (bytesTransferred == 0) {
            activityStruct = null;
            Intent intent = new Intent(ACTION_SYNC_COMPLETED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }
    }

    private GregorianCalendar parseTimestamp(byte[] data, int offset) {
        GregorianCalendar timestamp = new GregorianCalendar(
                data[offset] + 2000,
                data[offset + 1],
                data[offset + 2],
                data[offset + 3],
                data[offset + 4],
                data[offset + 5]);
        return timestamp;
    }

}