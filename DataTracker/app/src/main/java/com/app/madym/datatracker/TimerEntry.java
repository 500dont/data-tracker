package com.app.madym.datatracker;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimerEntry implements Parcelable {

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

    public TimerEntry(Parcel in) {
        mCategory = in.readString();
        mTimestamp = in.readLong();
        mState = in.readInt();
        mEntries = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mCategory);
        out.writeLong(mTimestamp);
        out.writeInt(mState);
        out.writeList(mEntries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TimerEntry createFromParcel(Parcel in) {
            return new TimerEntry(in);
        }

        public TimerEntry[] newArray(int size) {
            return new TimerEntry[size];
        }
    };

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

    public static String getTimeString(long millis) {
        final long second = (millis / 1000) % 60;
        final long minute = (millis / (1000 * 60)) % 60;
        final long hour = (millis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public static String getDateString(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(millis));
    }
}
