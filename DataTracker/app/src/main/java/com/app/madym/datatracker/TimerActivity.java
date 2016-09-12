package com.app.madym.datatracker;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    private TimerAdapter mAdapter;
    private ArrayList<TimerEntry> mTimerEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);
        setTitle(R.string.timer_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setElevation(0); // get rid of ugs shadow
        setSupportActionBar(toolbar);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        mTimerEntries = new ArrayList<>();
        mAdapter = new TimerAdapter(this, mTimerEntries);
        list.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            TimerEntry entry = new TimerEntry("SLEEP", 0);
            mTimerEntries.add(entry);
            mAdapter.notifyItemInserted(mTimerEntries.size()-1);
        }
    }

    public class TimerAdapter extends RecyclerView.Adapter<TimerHolder> {

        private final List<TimerEntry> mTimers;
        private Context mContext;
        private LayoutInflater mInflater;

        public TimerAdapter(Context context, List<TimerEntry> timers) {
            mInflater = LayoutInflater.from(context);
            mTimers = timers;
            mContext = context;
        }

        @Override
        public TimerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.timer_item, parent, false);
            return new TimerHolder(mContext, view);
        }

        @Override
        public void onBindViewHolder(TimerHolder holder, int position) {
            holder.bindEntry(mTimers.get(position));
        }

        @Override
        public int getItemCount() {
            return mTimers.size();
        }
    }

    public class TimerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mCategoryText;
        TextView mTotalText;
        TextView mTimerText;
        TextView mAction;
        TimerEntry mEntry;

        Handler mHandler;
        Runnable mTimeRunnable;
        boolean mTiming;

        public TimerHolder(Context ctx, View view) {
            super(view);
            mHandler = new Handler();
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    mTimerText.setText(mEntry.getTimerString());
                    mHandler.postDelayed(this, 1000);
                }
            };
            mCategoryText = (TextView) view.findViewById(R.id.category_text);
            mTotalText = (TextView) view.findViewById(R.id.total_text);
            mTimerText = (TextView) view.findViewById(R.id.timer_text);
            mAction = (TextView) view.findViewById(R.id.action);
            itemView.setOnClickListener(this);
        }

        private void updateViewForTiming() {
            if (mEntry.isTiming() && !mTiming) {
                mTiming = true;
                mHandler.post(mTimeRunnable);
                mTimerText.setVisibility(View.VISIBLE);
            } else {
                mTiming = false;
                mHandler.removeCallbacks(mTimeRunnable);
                mTimerText.setVisibility(View.INVISIBLE);
                mTotalText.setText(mEntry.getTotalTimeString());
            }
        }

        public void bindEntry(TimerEntry entry) {
            mEntry = entry;
            mCategoryText.setText(entry.getCategory());
            mTotalText.setText(mEntry.getTotalTimeString());
            updateViewForTiming();
        }

        @Override
        public void onClick(View v) {
            // Whenever the view is clicked, toggle the timer
            mEntry.setState(mEntry.isTiming() ? TimerEntry.NOT_TIMING : TimerEntry.TIMING);
            updateViewForTiming();
        }
    }
}
