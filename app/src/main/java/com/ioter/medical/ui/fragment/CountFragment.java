package com.ioter.medical.ui.fragment;

import android.view.View;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

/**
 * 医废统计
 */
public class CountFragment extends BaseFragment {
    public static CountFragment newInstance() {
        return new CountFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_count;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init(View view) {

    }

    @Override
    public void setBarCode(String barCode) {
    }
}
