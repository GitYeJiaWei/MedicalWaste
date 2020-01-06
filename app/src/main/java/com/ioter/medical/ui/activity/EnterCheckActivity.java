package com.ioter.medical.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ioter.medical.R;
import com.ioter.medical.di.component.AppComponent;
import com.ioter.medical.ui.fragment.EnterCheckFragment;
import com.ioter.medical.ui.fragment.EnterSureFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class EnterCheckActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private List<String> tabList = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_enter_check;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {
    }

    @Override
    public void init() {
        title.setText("入库确认");

        addData();
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());

        //viewpager设置适配器
        viewpager.setAdapter(tabAdapter);
        //必须在viewpager设置适配器后调用
        tablayout.setupWithViewPager(viewpager);
    }

    class TabAdapter extends FragmentPagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                //待确认
                fragment = EnterCheckFragment.newInstance();
            } else {
                //已确认
                fragment = EnterSureFragment.newInstance();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabList.get(position);
        }
    }

    private void addData() {
        tabList.add("待确认");
        tabList.add("已确认");
    }
}
