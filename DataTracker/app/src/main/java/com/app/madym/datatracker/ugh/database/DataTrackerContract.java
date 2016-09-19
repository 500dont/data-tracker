package com.app.madym.datatracker.ugh.database;

import android.provider.BaseColumns;

public final class DataTrackerContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataTrackerContract() {}

    // category + start = key
    public static class TimerEntry implements BaseColumns {
        public static final String TABLE_NAME = "timer_entries";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_END = "end";
    }

    public static class TimerTotals implements BaseColumns {
        public static final String TABLE_NAME = "timer_totals";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_TOTAL = "total";
    }
}
