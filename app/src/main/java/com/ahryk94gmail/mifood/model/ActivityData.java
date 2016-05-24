package com.ahryk94gmail.mifood.model;

public class ActivityData {

    public static final int TYPE_DEEP_SLEEP = 5;
    public static final int TYPE_LIGHT_SLEEP = 4;
    public static final int TYPE_ACTIVITY = -1;

    public static final byte PROVIDER_MIBAND = 0;

    private int mTimestamp;
    private int mProvider;
    private int mIntensity;
    private int mSteps;
    private int mType;

    public ActivityData(int timestamp, int provider, int intensity, int steps, int type) {
        this.mTimestamp = timestamp;
        this.mProvider = provider;
        this.mIntensity = intensity;
        this.mSteps = steps;
        this.mType = type;
    }

    public int getTimestamp() {
        return mTimestamp;
    }

    public int getProvider() {
        return mProvider;
    }

    public int getIntensity() {
        return mIntensity;
    }

    public int getSteps() {
        return mSteps;
    }

    public int getType() {
        return mType;
    }
}
