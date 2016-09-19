package com.app.madym.datatracker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EntryActivity extends BaseActivity {

    public static final String BUNDLE_KEY_ENTRY = "timerEntry";

    private EntryAdapter mAdapter;
    private TimerCategory mTimerCategory;
    private ArrayList<TimerCategory.Entry> mEntries;

    @Override
    public void init() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            mTimerCategory = data.getParcelable(BUNDLE_KEY_ENTRY);
        }
        if (mTimerCategory != null) {
            setTitle(mTimerCategory.getCategory());
            mEntries = mTimerCategory.getEntries();
        }
    }

    @Override
    public void createAndSetAdapter(RecyclerView list) {
        if (mTimerCategory != null) {
            mAdapter = new EntryAdapter(this, mEntries);
            list.setAdapter(mAdapter);
        }
    }

    @Override
    public void configureFab(FloatingActionButton fab) {
        // No fab when viewing entries
        if (fab != null) fab.setVisibility(View.GONE);
    }

    public class EntryAdapter extends RecyclerView.Adapter<EntryHolder> {

        private final ArrayList<TimerCategory.Entry> mEntries;
        private Context mContext;
        private LayoutInflater mInflater;

        public EntryAdapter(Context context, ArrayList<TimerCategory.Entry> entries) {
            mInflater = LayoutInflater.from(context);
            mEntries = entries;
            mContext = context;
        }

        @Override
        public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.entry_item, parent, false);
            return new EntryHolder(mContext, view);
        }

        @Override
        public void onBindViewHolder(EntryHolder holder, int position) {
            holder.bindEntry(mEntries.get(position));
        }

        @Override
        public int getItemCount() {
            return mEntries.size();
        }
    }

    public class EntryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TimerCategory.Entry mEntry;
        TextView mDateText;
        TextView mTimeText;

        public EntryHolder(Context ctx, View view) {
            super(view);
            mDateText = (TextView) view.findViewById(R.id.date_text);
            mTimeText = (TextView) view.findViewById(R.id.time_text);
            itemView.setOnClickListener(this);
        }

        public void bindEntry(TimerCategory.Entry entry) {
            mEntry = entry;
            mDateText.setText(TimerCategory.getDateString(mEntry.first));
            mTimeText.setText(TimerCategory.getTimeString(mEntry.second));
        }

        @Override
        public void onClick(View v) {

        }
    }

}
