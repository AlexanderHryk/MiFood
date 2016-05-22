package com.ahryk94gmail.mifood.miband;

import java.util.UUID;

public class ReadAction implements IBleAction {

    protected UUID serviceUUID;
    protected UUID characteristicUUID;

    public ReadAction(MiBandValue value) {
        this.serviceUUID = Constants.services.UUID_SERVICE_MILI;

        if (value == MiBandValue.STEPS) {
            this.characteristicUUID = Constants.characteristics.UUID_CHAR_REALTIME_STEPS;
        }
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        if (provider.isConnected()) {
            provider.readCharacteristic(this.serviceUUID, this.characteristicUUID);
            return true;
        }

        return false;
    }
}
