package com.ahryk94gmail.mifood.miband;

public class WaitAction implements IBleAction {

    private long mDelay;

    public WaitAction(long delay) {
        this.mDelay = delay;
    }

    @Override
    public boolean execute(IBleProvider provider) throws InterruptedException {
        Thread.sleep(this.mDelay);
        return false;
    }
}
