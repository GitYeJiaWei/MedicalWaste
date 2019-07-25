package com.ioter.medical.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ioter.medical.AppApplication;
import com.ioter.medical.common.CustomProgressDialog;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.presenter.BasePresenter;
import com.ioter.medical.ui.BaseView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment<T extends BasePresenter> extends BackPressedFragment implements BaseView
{

    private Unbinder mUnbinder;

    private AppApplication mApplication;

    private View mRootView;

    protected AppCompatActivity mActivity;

    protected CustomProgressDialog progressDialog;


    @Inject
    public T mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        mRootView = inflater.inflate(setLayout(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mActivity = (AppCompatActivity) this.getActivity();
        init(mRootView);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        this.mApplication = (AppApplication) getActivity().getApplication();
        setupAcitivtyComponent(mApplication.getAppComponent());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mUnbinder != Unbinder.EMPTY)
        {
            mUnbinder.unbind();
        }
    }

    public void myOnKeyDwon()
    {

    }

    public void myOnKeyUp()
    {

    }

    @Override
    public void showLoading()
    {
    }

    @Override
    public void showError(String msg)
    {
    }

    @Override
    public void dismissLoading()
    {
    }

    public abstract int setLayout();

    public abstract void setupAcitivtyComponent(AppComponent appComponent);


    public abstract void init(View view);

    public abstract void setBarCode(String barCode);


    @Override
    public void onBackPressed()
    {
        mActivity.onBackPressed();
    }

}
