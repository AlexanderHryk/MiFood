package com.ahryk94gmail.mifood.miband;

import java.util.UUID;

public class SetCharaNotificationAction implements IBleAction {

    protected UUID serviceUUID;
    protected UUID characteristicUUID;

    protected SetCharaNotificationAction(UUID serviceUUID, UUID characteristicUUID) {
        this.serviceUUID = serviceUUID;
        this.characteristicUUID = characteristicUUID;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        if (provider.isConnected()) {
            provider.setCharacteristicNotification(this.serviceUUID, this.characteristicUUID);
            return true;
        }

        return false;
    }
}
