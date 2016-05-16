package com.ahryk94gmail.mifood.presenter;

public interface IScanPresenter {

    void startScan();

    void stopScan();

    void bindDevice(int pos, String address);

    boolean isScanning();

    void onDestroy();
}
