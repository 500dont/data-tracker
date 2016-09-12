package com.app.madym.datatracker;

import java.util.Calendar;

public class TimerEntry {

    public static final int TIMING = 0;
    public static final int PAUSED = 1;
    public static final int NOT_TIMING = 2;

    private String mCategory;
    private int mTotal;
    private long mTimestamp; // If timing when it started
    private int mState;

    public TimerEntry(String category, int total) {
        mCategory = category;
        mTotal = total;
        mState = NOT_TIMING;
    }

    public void setState(int state) {
        if (state != mState) {
            mState = state;
            switch(mState) {
                case TIMING:
                    mTimestamp = Calendar.getInstance().getTimeInMillis();
                    break;

                case PAUSED:
                    // Don't do anything for paused right now
                    break;

                case NOT_TIMING:
                    mTotal += Calendar.getInstance().getTimeInMillis() - mTimestamp;
                    break;
            }
        }
    }

    public String getCategory() {
        return mCategory;
    }

    public String getTotalTimeString() {
        return getTimeString(mTotal);
    }

    public boolean isTiming() {
        return mState == TIMING;
    }

    public String getTimerString() {
        if (isTiming()) {
            final long millis = Calendar.getInstance().getTimeInMillis() - mTimestamp;
            return getTimeString(millis);
        }
        return null;
    }

    private String getTimeString(long millis) {
        final long second = (millis / 1000) % 60;
        final long minute = (millis / (1000 * 60)) % 60;
        final long hour = (millis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
