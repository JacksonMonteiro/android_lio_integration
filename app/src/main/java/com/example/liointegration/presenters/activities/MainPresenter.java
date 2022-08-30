package com.example.liointegration.presenters.activities;

import com.example.liointegration.views.activities.view.MainView;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

public class MainPresenter extends MvpBasePresenter<MainView> {
    public void back(boolean checkFragment) {
        if (checkFragment)
            getView().back();
    }
}
