package com.ahryk94gmail.mifood.miband;

public class SetHeartRateNotificationAction extends SetCharaNotificationAction {
    public SetHeartRateNotificationAction() {
        super(Constants.services.UUID_SERVICE_HEART_RATE, Constants.characteristics.UUID_CHAR_HEART_RATE);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
