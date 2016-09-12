package com.app.madym.datatracker;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;

public class TimerEntry {

    public static final int TIMING = 0;
    public static final int PAUSED = 1;
    public static final int NOT_TIMING = 2;

    private String mCategory;
    private long mTimestamp; // When the timing started (if timing)
    private int mState;
    private ArrayList<Pair<Long, Long>> mEntries; // first = date, second = amount of time

    public TimerEntry(String category) {
        mCategory = category;
        mState = NOT_TIMING;
        mEntries = new ArrayList<>();
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
                    final long add = Calendar.getInstance().getTimeInMillis() - mTimestamp;
                    mEntries.add(new Pair(mTimestamp, add));
                    break;
            }
        }
    }

    public void setEntries(ArrayList<Pair<Long, Long>> entries) {
        mEntries = entries;
    }

    public ArrayList<Pair<Long, Long>> getEntries() {
        return mEntries;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getTotalTimeString() {
        return getTimeString(getTotalTime());
    }

    public long getTotalTime() {
        long total = 0;
        for (int i = 0; i < mEntries.size(); i++) {
            total += mEntries.get(i).second;
        }
        return total;
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
