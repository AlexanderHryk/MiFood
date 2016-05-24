package com.ahryk94gmail.mifood.miband;

public class StartSyncAction extends WriteAction {
    public StartSyncAction() {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_CONTROL_POINT, Constants.protocol.FETCH_ACTIVITY_DATA);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
