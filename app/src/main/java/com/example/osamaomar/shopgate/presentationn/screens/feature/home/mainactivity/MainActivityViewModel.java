package com.example.osamaomar.shopgate.presentationn.screens.feature.home.mainactivity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.osamaomar.shopgate.domain.ServerGateway;
import com.example.osamaomar.shopgate.entities.Sidemenu;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


class MainActivityViewModel extends ViewModel {


    public MutableLiveData<Sidemenu> sidemenuMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Throwable> throwableMutableLiveData = new MutableLiveData<>();
    private ServerGateway serverGateway;

     MainActivityViewModel(ServerGateway serverGateway1) {
        serverGateway = serverGateway1;
        getData();
    }

    public void getData() {
        getObservable().subscribeWith(getObserver());
    }

    @SuppressLint("CheckResult")
    private Observable<Sidemenu> getObservable() {
        Observable<Sidemenu> photographersData = serverGateway.getSideMenuData();
        photographersData.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return photographersData;
    }

    private DisposableObserver<Sidemenu> getObserver() {
        return new DisposableObserver<Sidemenu>() {
            @Override
            public void onNext(@NonNull Sidemenu result) {
                if (sidemenuMutableLiveData !=null)
                    sidemenuMutableLiveData.postValue(result);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("Errors", "Error" + e);
                e.printStackTrace();
                throwableMutableLiveData.postValue(e);
            }
            @Override
            public void onComplete() {
            }
        };
    }
}
