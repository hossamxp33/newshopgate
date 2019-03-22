package com.example.osamaomar.shopgate.presentationn.screens.feature.home.mainfragment;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.osamaomar.shopgate.domain.ApiClient;
import com.example.osamaomar.shopgate.domain.ServerGateway;

public class MainViewModelFactory implements ViewModelProvider.Factory {


    private Application application;


    public MainViewModelFactory(Application application1) {
        application = application1;

    }

    @SuppressWarnings("SingleStatementInBlock")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
         if (modelClass == MainFragmentViewModel.class)
        {
            return (T) new MainFragmentViewModel(getApiService());
        }

        throw new IllegalArgumentException("Invalid view model class type");
    }


    private ServerGateway getApiService() {
        return ApiClient.getClient().create(ServerGateway.class);
    }

}
