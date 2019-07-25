package com.ioter.medical.ui.fragment;

import android.view.View;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;

/**
 * 医废查询
 */
public class CheckFragment extends BaseFragment {
    public static CheckFragment newInstance() {
        return new CheckFragment();
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_check;
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
