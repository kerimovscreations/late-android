package com.kerimovscreations.lateandroid.enums;

public enum SoundType {
    MALE_NORMAL(0), FEMALE_NORMAL(1),
    MALE_FUNNY_1(2), FEMALE_FUNNY_1(3),
    MALE_FUNNY_2(4), FEMALE_FUNNY_2(5);

    private final int mValue;

    SoundType(int value) {
        this.mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
