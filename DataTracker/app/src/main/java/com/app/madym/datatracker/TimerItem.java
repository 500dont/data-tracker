package com.app.madym.datatracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimerItem implements Parcelable {

    private static final int UND = -1;

    public static final int TIMING = 0;
    public static final int NOT_TIMING = 1;
    public static final int STOPPED = 2;
    public static final int CANCELLED = 3;

    private String mItemName;
    private long mTimerStart;
    private long mTimerEnd;
    private int mState;
    private ArrayList<Entry> mEntries; // startTime = date, endTime = amount of time

    public TimerItem(String name) {
        mItemName = name;
        mState = NOT_TIMING;
        mEntries = new ArrayList<>();
    }

    public TimerItem(Parcel in) {
        mItemName = in.readString();
        mTimerStart = in.readLong();
        mState = in.readInt();
        mEntries = in.readArrayList(TimerItem.Entry.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(mItemName);
        out.writeLong(mTimerStart);
        out.writeInt(mState);
        out.writeList(mEntries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TimerItem createFromParcel(Parcel in) {
            return new TimerItem(in);
        }

        public TimerItem[] newArray(int size) {
            return new TimerItem[size];
        }
    };

    public void setState(int state) {
        if (state != mState) {
            mState = state;
            switch(mState) {
                case TIMING:
                    mTimerStart = Calendar.getInstance().getTimeInMillis();
                    mTimerEnd = UND;
                    break;

                case STOPPED:
                    mTimerEnd = Calendar.getInstance().getTimeInMillis();
                    break;

                case NOT_TIMING:
                    if (mTimerStart != UND && mTimerEnd != UND) {
                        mEntries.add(new Entry(mTimerStart, mTimerEnd));
                    }
                    break;

                case CANCELLED:
                    mTimerStart = UND;
                    mTimerEnd = UND;
                    break;
            }
        }
    }

    public ArrayList<Entry> getEntries() {
        return mEntries;
    }

    public void setItemName(String name) {
        mItemName = name;
    }

    public String getItemName() {
        return mItemName;
    }

    public long getStartTime() {
        return mTimerStart;
    }

    public long getTotalTime() {
        long total = 0;
        for (int i = 0; i < mEntries.size(); i++) {
            total += mEntries.get(i).getTotalTime();
        }
        return total;
    }

    public boolean isTiming() {
        return mState == TIMING;
    }

    public boolean isStopped() {
        return mState == STOPPED;
    }

    public String getTimerString() {
        if (isTiming()) {
            final long millis = Calendar.getInstance().getTimeInMillis() - mTimerStart;
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
        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM h:mm a");
        return formatter.format(new Date(millis));
    }

    public static class Entry implements Parcelable {

        public long startTime;
        public long endTime;

        public Entry(long startTime, long endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Entry(Parcel in) {
            startTime = in.readLong();
            endTime = in.readLong();
        }

        public long getTotalTime() {
            return endTime - startTime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int i) {
            out.writeLong(startTime);
            out.writeLong(endTime);
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Entry createFromParcel(Parcel in) {
                return new Entry(in);
            }

            public Entry[] newArray(int size) {
                return new Entry[size];
            }
        };
    }
}
