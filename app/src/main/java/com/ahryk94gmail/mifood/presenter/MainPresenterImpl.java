package com.ahryk94gmail.mifood.presenter;

import android.content.Intent;

import com.ahryk94gmail.mifood.R;
import com.ahryk94gmail.mifood.miband.BleActionExecutionService;
import com.ahryk94gmail.mifood.miband.BleActionExecutionServiceControlPoint;
import com.ahryk94gmail.mifood.miband.SetUserProfileAction;
import com.ahryk94gmail.mifood.model.UserProfile;
import com.ahryk94gmail.mifood.view.IMainView;

public class MainPresenterImpl implements IMainPresenter {

    private static final boolean DBG = true;

    private IMainView mMainView;
    private BleActionExecutionServiceControlPoint mControlPoint;

    public MainPresenterImpl(IMainView view) {
        this.mMainView = view;
        this.mControlPoint = BleActionExecutionServiceControlPoint.getInstance(view.getContext());
    }


    @Override
    public void setUserProfile() {
        //TODO
    }

    @Override
    public void setUserProfile(int pairingResultCode, Intent data) {
        if (pairingResultCode == BleActionExecutionService.EXTRA_PAIRING_RESULT_CODE_SUCCESS) {
            UserProfile userProfile = new UserProfile(10000000, UserProfile.GENDER_MALE, 21, 182, 76, "Alex", 1);
            String deviceAddress = data.getStringExtra(BleActionExecutionService.EXTRA_DEVICE_ADDRESS);
            this.mControlPoint.addToQueue(new SetUserProfileAction(userProfile, deviceAddress));
        } else {
            this.mMainView.showMessage(this.mMainView.getContext().getResources().getString(R.string.coupling_error) + "\t(" + pairingResultCode + ")");
        }
    }

    @Override
    public void onDestroy() {
        this.mMainView = null;
    }
}
