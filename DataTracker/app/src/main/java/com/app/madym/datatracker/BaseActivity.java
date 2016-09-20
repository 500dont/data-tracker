package com.app.madym.datatracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

// Activity that displays a list and a FAB, contains common functionality
// for current and future activities.
public class BaseActivity extends AppCompatActivity {

    ViewGroup mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        setTitle(R.string.timer_activity);

        mRoot = (ViewGroup) findViewById(R.id.root);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setElevation(0); // get rid of ugs shadow
        setSupportActionBar(toolbar);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        init();

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true); // if rows become different heights, must remove
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        createAndSetAdapter(list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        configureFab(fab);
    }

    // Overriding classes should put anything needed to initialize the
    // adapter or restore activity state here.
    public void init() {
    }

    // Overriding classes should create and populate the recycler list
    // adapter (if desired) here.
    public void createAndSetAdapter(RecyclerView list) {
    }

    // Overriding classes can set listeners / do things to the fab here.
    public void configureFab(FloatingActionButton fab) {
    }
}
