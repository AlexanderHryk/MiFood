package com.ahryk94gmail.mifood.model;

import java.util.ArrayList;
import java.util.List;

public class ActivityAmounts {

    private List<ActivityAmount> mAmounts = new ArrayList<>(3);
    private long mTotalSeconds;

    public void addAmount(ActivityAmount amount) {
        this.mAmounts.add(amount);
        this.mTotalSeconds += amount.getTotalSeconds();
    }

    public List<ActivityAmount> getAmounts() {
        return this.mAmounts;
    }

    public long getTotalSeconds() {
        return this.mTotalSeconds;
    }

    public void calculatePercentages() {
        for (ActivityAmount amount : this.mAmounts) {
            float fraction = amount.getTotalSeconds() / (float) this.mTotalSeconds;
            amount.setPercent((short) (fraction * 100));
        }
    }
}
