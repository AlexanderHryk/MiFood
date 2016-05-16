package com.ahryk94gmail.mifood.miband;

import com.ahryk94gmail.mifood.model.UserProfile;

public class SetUserProfileAction extends WriteAction {

    public SetUserProfileAction(UserProfile userProfile, String deviceAddress) {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_USER_INFO);

        if (userProfile == null)
            throw new NullPointerException("userProfile");

        super.data = userProfile.getBytes(deviceAddress);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
