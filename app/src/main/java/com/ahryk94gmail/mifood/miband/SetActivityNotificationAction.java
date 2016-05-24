package com.ahryk94gmail.mifood.miband;

public class SetActivityNotificationAction extends SetCharaNotificationAction {
    public SetActivityNotificationAction() {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_ACTIVITY);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
