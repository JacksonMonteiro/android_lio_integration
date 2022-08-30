package com.example.liointegration.views.activities;

import androidx.annotation.NonNull;

import com.example.liointegration.R;
import com.example.liointegration.views.activities.view.MainView;
import com.example.liointegration.presenters.activities.MainPresenter;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView {

    @Override
    public void initView() {}

    @Override
    public void initActions() {
        super.initActions();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
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