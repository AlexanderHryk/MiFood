package com.ahryk94gmail.mifood.view;

import android.content.Context;
import android.content.Intent;

public interface IScanView {

    void addDeviceInfoItem(String name, String address);

    void setItemSubtitle(int pos, String text);

    void setEnabled(boolean enabled);

    void clear();

    void startProgressAnimation();

    void stopProgressAnimation();

    void returnResult(int resultCode, Intent data);

    void finish();

    void showMessage(String message);

    Context getContext();
}
