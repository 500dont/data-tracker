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

// Activity that displays a list of entries, i.e. all entries of a particular item
public class EntryActivity extends BaseActivity {

    // Used to retrieve the item from intent to populate the adapter
    public static final String BUNDLE_KEY_ENTRY = "entryBundleKey";

    private TimerItem mTimerItems;
    private ArrayList<TimerItem.Entry> mEntries;

    @Override
    public void init() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            // Retrieve list of entries
            mTimerItems = data.getParcelable(BUNDLE_KEY_ENTRY);
        }
        if (mTimerItems != null) {
            setTitle(mTimerItems.getItemName());
            mEntries = mTimerItems.getEntries();
        }
    }

    @Override
    public void createAndSetAdapter(RecyclerView list) {
        if (mTimerItems != null) {
            EntryAdapter adapter = new EntryAdapter(this, mEntries);
            list.setAdapter(adapter);
        }
    }

    @Override
    public void configureFab(FloatingActionButton fab) {
        // No fab when viewing entries
        if (fab != null) fab.setVisibility(View.GONE);
    }

    public class EntryAdapter extends RecyclerView.Adapter<EntryHolder> {

        private final ArrayList<TimerItem.Entry> mEntries;
        private Context mContext;
        private LayoutInflater mInflater;

        public EntryAdapter(Context context, ArrayList<TimerItem.Entry> entries) {
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

        TimerItem.Entry mEntry;
        TextView mDateText;
        TextView mTimeText;

        public EntryHolder(Context ctx, View view) {
            super(view);
            mDateText = (TextView) view.findViewById(R.id.date_text);
            mTimeText = (TextView) view.findViewById(R.id.time_text);
            itemView.setOnClickListener(this);
        }

        public void bindEntry(TimerItem.Entry entry) {
            mEntry = entry;
            mDateText.setText(TimerItem.getDateString(mEntry.startTime));
            mTimeText.setText(TimerItem.getTimeString(mEntry.getTotalTime()));
        }

        @Override
        public void onClick(View v) {

        }
    }

}
