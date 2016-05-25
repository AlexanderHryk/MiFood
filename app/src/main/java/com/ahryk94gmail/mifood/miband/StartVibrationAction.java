package com.ahryk94gmail.mifood.miband;

public class StartVibrationAction extends WriteAction {
    public StartVibrationAction(VibrationMode mode) {
        super(Constants.services.UUID_SERVICE_VIBRATION, Constants.characteristics.UUID_CHAR_VIBRATION, Constants.protocol.STOP_VIBRATION);

        if (mode == VibrationMode.VIBRATION_WITH_LED) {
            super.data = Constants.protocol.VIBRATION_WITH_LED;
        } else if (mode == VibrationMode.VIBRATION_10_TIMES_WITH_LED) {
            super.data = Constants.protocol.VIBRATION_10_TIMES_WITH_LED;
        } else if (mode == VibrationMode.VIBRATION_WITHOUT_LED) {
            super.data = Constants.protocol.VIBRATION_WITHOUT_LED;
        }
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        return super.execute(provider);
    }
}
