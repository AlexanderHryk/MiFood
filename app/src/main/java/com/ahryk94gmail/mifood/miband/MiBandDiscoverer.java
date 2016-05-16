package com.ahryk94gmail.mifood.miband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

import com.ahryk94gmail.mifood.Debug;

public class MiBandDiscoverer implements IBleDeviceDiscoverer, BluetoothAdapter.LeScanCallback {

    private static final boolean DBG = false;
    private static final String MAC_ADDRESS_FILTER_MI1A = Constants.type.MI1A;
    private static final String MAC_ADDRESS_FILTER_MI1S = Constants.type.MI1S;
    private static final int SCAN_PERIOD = 30000;

    private IBleDeviceDiscoverer.OnDeviceDiscoveredCallback mOnDeviceDiscoveredCallback;
    private IBleDeviceDiscoverer.OnCanceledCallback mOnCanceledCallback;
    private boolean mIsDiscovering = false;
    private BluetoothAdapter mAdapter;
    private Context mContext;
    private Handler mHandler;

    public MiBandDiscoverer(Context context) {
        this.mContext = context;
        this.mAdapter = ((BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        this.mHandler = new Handler();
    }

    public MiBandDiscoverer(Context context, IBleDeviceDiscoverer.OnDeviceDiscoveredCallback onDeviceDiscoveredCallback) {
        this(context);
        setOnDeviceDiscoveredCallback(onDeviceDiscoveredCallback);
    }

    public MiBandDiscoverer(Context context, IBleDeviceDiscoverer.OnDeviceDiscoveredCallback onDeviceDiscoveredCallback, IBleDeviceDiscoverer.OnCanceledCallback onCanceledCallback) {
        this(context, onDeviceDiscoveredCallback);
        setOnCanceledCallback(onCanceledCallback);
    }

    private Runnable mCancelDiscovering = new Runnable() {
        @Override
        public void run() {
            cancelDiscovering(true);
        }
    };

    @Override
    public boolean discover() {
        if (isDiscovering()) {
            return false;
        }

        boolean isScanStarted;

        if (isScanStarted = this.mAdapter.startLeScan(this)) {
            Debug.WriteLog(DBG, "discovering: started");
            this.mIsDiscovering = true;
            this.mHandler.postDelayed(this.mCancelDiscovering, SCAN_PERIOD);
        }

        return isScanStarted;
    }

    @Override
    public boolean isDiscovering() {
        return this.mIsDiscovering;
    }

    @Override
    public void cancelDiscovering() {
        if (!isDiscovering()) {
            return;
        }

        this.mHandler.removeCallbacks(this.mCancelDiscovering);
        cancelDiscovering(false);
    }

    private void cancelDiscovering(boolean timeout) {
        this.mAdapter.stopLeScan(this);
        this.mIsDiscovering = false;
        if (this.mOnCanceledCallback != null)
            this.mOnCanceledCallback.onCanceled(timeout);
        Debug.WriteLog(DBG, "discovering: canceled");
    }

    @Override
    public void setOnDeviceDiscoveredCallback(OnDeviceDiscoveredCallback callback) {
        this.mOnDeviceDiscoveredCallback = callback;
    }

    @Override
    public void setOnCanceledCallback(OnCanceledCallback callback) {
        this.mOnCanceledCallback = callback;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getAddress().startsWith(MAC_ADDRESS_FILTER_MI1A) || device.getAddress().startsWith(MAC_ADDRESS_FILTER_MI1S)) {
            Debug.WriteLog(DBG, "detected: " + device.getName() + " (" + device.getAddress() + ")");

            if (this.mOnDeviceDiscoveredCallback != null) {
                this.mOnDeviceDiscoveredCallback.onDeviceDiscovered(device);
            }
        }
    }
}
