package com.ahryk94gmail.mifood.miband;

public class SendAckAction extends WriteAction {
    public SendAckAction(byte[] ack) {
        super(Constants.services.UUID_SERVICE_MILI, Constants.characteristics.UUID_CHAR_CONTROL_POINT, ack);
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
