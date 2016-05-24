package com.ahryk94gmail.mifood.model;

public class ActivityAmount {
    private int mActivityType;
    private short mPercent;
    private long mTotalSeconds;

    public ActivityAmount(int activityType) {
        this.mActivityType = activityType;
    }

    public void addSeconds(long seconds) {
        this.mTotalSeconds += seconds;
    }

    public long getTotalSeconds() {
        return this.mTotalSeconds;
    }

    public int getActivityType() {
        return this.mActivityType;
    }

    public short getPercent() {
        return this.mPercent;
    }

    public void setPercent(short percent) {
        this.mPercent = percent;
    }

    public String getName() {
        switch (getActivityType()) {
            case ActivityData.TYPE_DEEP_SLEEP:
                return "Deep Sleep";
            case ActivityData.TYPE_LIGHT_SLEEP:
                return "Light Sleep";
        }
        return "Activity";
    }
}
