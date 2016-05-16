package com.ahryk94gmail.mifood.presenter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.ahryk94gmail.mifood.Debug;
import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.UserPreferences;
import com.ahryk94gmail.mifood.miband.BleActionExecutionService;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.ConnectAction;
import com.ahryk94gmail.mifood.miband.IBleAction;
import com.ahryk94gmail.mifood.miband.IBleDeviceDiscoverer;
import com.ahryk94gmail.mifood.miband.MiBandDiscoverer;
import com.ahryk94gmail.mifood.miband.PairAction;
import com.ahryk94gmail.mifood.view.IScanView;

import java.util.LinkedList;
import java.util.List;

public class ScanPresenterImpl implements IScanPresenter {

    private static final boolean DBG = true;

    private IScanView mScanView;
    private IBleDeviceDiscoverer mDiscoverer;

    private BleActionExecutionServiceControlPoint mControlPoint;
    private BroadcastReceiver mPairingResultCodeReceiver;
    private boolean mIsDeviceSelected = false;

    public ScanPresenterImpl(IScanView view) {
        this.mScanView = view;
        this.mControlPoint = new BleActionExecutionServiceControlPoint(view.getContext());
        this.mControlPoint.bind();

        this.mDiscoverer = new MiBandDiscoverer(view.getContext());

        this.mDiscoverer.setOnDeviceDiscoveredCallback(new IBleDeviceDiscoverer.OnDeviceDiscoveredCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDevice device) {
                if (mScanView == null)
                    return;

                ScanPresenterImpl.this.mScanView.addDeviceInfoItem(device.getName(), device.getAddress());
            }
        });

        this.mDiscoverer.setOnCanceledCallback(new IBleDeviceDiscoverer.OnCanceledCallback() {
            @Override
            public void onCanceled(boolean timeout) {
                ScanPresenterImpl.this.mScanView.stopProgressAnimation();
            }
        });

        this.mPairingResultCodeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int pairingResultCode = intent.getIntExtra(BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE, -1);

                if (pairingResultCode == BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE_SUCCESS) {
                    UserPreferences.getInstance().saveBondedDeviceAddress(context, intent.getStringExtra(BleActionExecutionService.EXTRA_DEVICE_ADDRESS));
                } else {
                    Debug.WriteLog(DBG, "EXTRA_PAIRING_RESULT_CODE = " + pairingResultCode);
                }

                mScanView.returnResult(pairingResultCode, intent);
                mScanView.finish();
            }
        };

        LocalBroadcastManager.getInstance(this.mScanView.getContext()).registerReceiver(this.mPairingResultCodeReceiver,
                new IntentFilter(BleActionExecutionService.ACTION_PAIRED));
    }

    @Override
    public void startScan() {
        if (!this.mIsDeviceSelected && this.mDiscoverer.discover()) {
            this.mScanView.clear();
            this.mScanView.startProgressAnimation();
        }
    }

    @Override
    public void stopScan() {
        this.mDiscoverer.cancelDiscovering();
    }

    @Override
    public void bindDevice(int pos, String address) {
        stopScan();
        this.mIsDeviceSelected = true;
        this.mScanView.setEnabled(false);
        this.mScanView.setItemSubtitle(pos, this.mScanView.getContext().getResources().getString(R.string.coupling));

        UserPreferences.getInstance().removeBondedDeviceAddress(this.mScanView.getContext());

        BluetoothDevice device = ((BluetoothManager) this.mScanView.getContext().getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter()
                .getRemoteDevice(address);

        List<IBleAction> bleActions = new LinkedList<>();
        bleActions.add(new ConnectAction(device));
        bleActions.add(new PairAction(this.mScanView.getContext(), device));

        this.mControlPoint.addToQueue(bleActions);
    }

    @Override
    public boolean isScanning() {
        return this.mDiscoverer.isDiscovering();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this.mScanView.getContext()).unregisterReceiver(this.mPairingResultCodeReceiver);
        this.mDiscoverer.cancelDiscovering();
        this.mDiscoverer = null;
        this.mScanView = null;

        //TODO disconnect device when it not paired but still connected
    }
}
