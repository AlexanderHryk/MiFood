package com.ahryk94gmail.mifood.miband;

public class SetRealtimeStepsNotificationAction extends SetCharaNotificationAction {

    public SetRealtimeStepsNotificationAction() {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_REALTIME_STEPS);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
