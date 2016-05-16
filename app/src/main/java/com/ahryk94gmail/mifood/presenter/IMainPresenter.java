package com.ahryk94gmail.mifood.presenter;

import android.content.Intent;

public interface IMainPresenter {
    void setUserProfile();

    void setUserProfile(int pairingResultCode, Intent data);

    void onDestroy();
}
