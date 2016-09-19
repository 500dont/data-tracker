package com.app.madym.datatracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends BaseActivity implements View.OnClickListener {

    private static final int UND = -1;

    private TimerAdapter mAdapter;
    private ArrayList<TimerCategory> mTimerCategories;

    @Override
    public void init() {
        setTitle(R.string.timer_activity);
    }

    @Override
    public void createAndSetAdapter(RecyclerView list) {
        mTimerCategories = new ArrayList<>();
        mAdapter = new TimerAdapter(this, mTimerCategories);
        list.setAdapter(mAdapter);
        mTimerCategories.add(new TimerCategory("Sleep"));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void configureFab(FloatingActionButton fab) {
        if (fab != null) fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            showEntryDialog(UND); // UND treats this as new entry
        }
    }

    public class TimerAdapter extends RecyclerView.Adapter<TimerHolder> {

        private final List<TimerCategory> mTimers;
        private Context mContext;
        private LayoutInflater mInflater;

        public TimerAdapter(Context context, List<TimerCategory> timers) {
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

    public class TimerHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        TextView mCategoryText;
        TextView mTotalText;
        TextView mTimerText;
        ImageView mAction1;
        ImageView mAction2;
        TimerCategory mEntry;

        Handler mHandler;
        Runnable mTimeRunnable;
        Runnable mBlinkRunnable;

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
            mBlinkRunnable = new Runnable() {
                @Override
                public void run() {
                    final boolean isVisible = mTimerText.getVisibility() == View.VISIBLE;
                    mTimerText.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
                    mHandler.postDelayed(this, 500);
                }
            };
            mCategoryText = (TextView) view.findViewById(R.id.category_text);
            mTotalText = (TextView) view.findViewById(R.id.total_text);
            mTimerText = (TextView) view.findViewById(R.id.timer_text);

            mAction1 = (ImageView) view.findViewById(R.id.action_1);
            mAction2 = (ImageView) view.findViewById(R.id.action_2);

            mAction1.setOnClickListener(this);
            mAction2.setOnClickListener(this);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private void updateViewForTiming() {
            // TODO this is gross
            if (mEntry.isTiming() && !mTiming) {
                mTiming = true;
                mHandler.post(mTimeRunnable);
                mAction1.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                mAction2.setEnabled(true);
                mTimerText.setVisibility(View.VISIBLE);
            } else {
                mAction1.setImageDrawable(getResources().getDrawable(
                        mTiming ? R.drawable.ic_done : R.drawable.ic_play));
                if (mTiming) {
                    // Blink!
                    mHandler.post(mBlinkRunnable);
                } else {
                    mHandler.removeCallbacks(mBlinkRunnable);
                    mAction2.setEnabled(false);
                }
                mTiming = false;
                mHandler.removeCallbacks(mTimeRunnable);
                mTimerText.setVisibility(View.INVISIBLE);
                mTotalText.setText(mEntry.getTotalTimeString());
            }
        }

        public void bindEntry(TimerCategory entry) {
            mEntry = entry;
            mCategoryText.setText(entry.getCategory());
            mTotalText.setText(mEntry.getTotalTimeString());
            updateViewForTiming();
        }

        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.action_1:
                    if (mEntry.isTiming()) {
                        mEntry.setState(TimerCategory.STOPPED);
                    } else if (mEntry.isStopped()) {
                        mEntry.setState(TimerCategory.NOT_TIMING); // This also saves the entry
                    } else {
                        mEntry.setState(TimerCategory.TIMING);
                    }
                    updateViewForTiming();
                    break;

                case R.id.action_2:
                    mEntry.setState(TimerCategory.CANCELLED);
                    updateViewForTiming();
                    break;

                default:
                    final int position = getAdapterPosition();
                    Intent i = new Intent(getApplicationContext(), EntryActivity.class);
                    i.putExtra(EntryActivity.BUNDLE_KEY_ENTRY, mTimerCategories.get(position));
                    startActivity(i);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            final int position = getAdapterPosition();
            showEntryDialog(position);
            return true;
        }
    }

    private void showEntryDialog(final int entryIndex) {
        // TODO: Something to do with style weirdness, text selection / handles don't use
        // accent colour as desired, what's up with that?
        final boolean isNew = entryIndex == UND;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppAlertDialog);
        builder.setTitle(isNew ? R.string.new_entry_title : R.string.update_entry_title);

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        final View dialogView =  inflater.inflate(R.layout.new_entry_dialog, mRoot, false);
        final EditText input = (EditText) dialogView.findViewById(R.id.input);
        if (!isNew) {
            // We're editing a current item so update input text with current name
            TimerCategory entry = mTimerCategories.get(entryIndex);
            input.setText(entry.getCategory());
        }

        builder.setView(dialogView);
        builder.setPositiveButton(isNew ? R.string.positive_button : R.string.positive_update_button,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String text = input.getText().toString();
                // TODO Should also check for unique name
                if (!TextUtils.isEmpty(text)) {
                    if (isNew) {
                        TimerCategory entry = new TimerCategory(text);
                        mTimerCategories.add(entry);
                        mAdapter.notifyItemInserted(mTimerCategories.size() - 1);
                    } else {
                        mTimerCategories.get(entryIndex).setCategory(text);
                        mAdapter.notifyItemChanged(entryIndex);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.invalid_name, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
