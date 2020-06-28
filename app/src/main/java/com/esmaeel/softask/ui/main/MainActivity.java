package com.esmaeel.softask.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.esmaeel.softask.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binder;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binder.getRoot());
    }


}