package com.example.contactsapp.ui.authentication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.contactsapp.ui.dashboard.viewmodel.DashboardViewModel;

public class DashboardViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;

    public DashboardViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DashboardViewModel(application);
    }
}
