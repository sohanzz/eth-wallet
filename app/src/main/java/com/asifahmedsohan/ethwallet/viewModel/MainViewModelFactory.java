package com.asifahmedsohan.ethwallet.viewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String address;

    public MainViewModelFactory(Application application, String param){
        mApplication = application;
        address = param;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        MainActivityViewModel mainActivityViewModel = new MainActivityViewModel(mApplication, address);
        return (T) mainActivityViewModel;
    }
}
