package com.example.liointegration.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.liointegration.R;
import com.example.liointegration.activities.views.MainView;
import com.example.liointegration.presenters.activities.MainPresenter;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {}

    @Override
    public void initActions() {
        super.initActions();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return null;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void back() {
        super.onBackPressed();
    }
}