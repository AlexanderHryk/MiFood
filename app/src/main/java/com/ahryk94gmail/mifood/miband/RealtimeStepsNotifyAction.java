package com.ahryk94gmail.mifood.miband;

public class RealtimeStepsNotifyAction extends WriteAction {

    public RealtimeStepsNotifyAction(boolean enable) {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_CONTROL_POINT);

        super.data = enable ? Constants.protocol.ENABLE_REALTIME_STEPS_NOTIFY
                : Constants.protocol.DISABLE_REALTIME_STEPS_NOTIFY;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
