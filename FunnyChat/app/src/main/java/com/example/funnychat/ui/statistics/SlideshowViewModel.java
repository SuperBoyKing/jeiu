package com.example.funnychat.ui.statistics;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.charts.PieChart;

public class SlideshowViewModel extends ViewModel {

    PieChart pieChart;
    private MutableLiveData<PieChart> pieChartMutableLiveData;

    public SlideshowViewModel() {
        pieChartMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<PieChart> getText() {
        return pieChartMutableLiveData;
    }
}