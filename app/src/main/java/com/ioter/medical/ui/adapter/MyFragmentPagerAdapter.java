package com.ioter.medical.ui.adapter;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by YJW on 2018/1/2.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private FragmentManager fm1;
    private List<String> titleList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titleLists) {
        super(fm);
        this.fm1 = fm;
        this.fragmentList = fragments;
        this.titleList = titleLists;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    /**
     * 当前item的Fragment
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * 获得viewpager中有多少个view
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //instantiateItem(): ①将给定位置的view添加到ViewGroup(容器)中,创建并显示出来
    //②返回一个代表新增页面的Object(key),通常都是直接返回view本身就可以了,
    // 当然你也可以 自定义自己的key,但是key和每个view要一一对应的关系
    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        Fragment fragment = null;
        fragment = (Fragment) super.instantiateItem(vg,position);
        return fragment;
    }

    /**
     * Viewpager报错Fragment no longer exists for key f0: index 0
     * 重写下面方法，返回空
     * @return
     */
    @Override
    public Parcelable saveState() {
        return null;
    }

    //destroyItem():移除一个给定位置的页面。适配器有责任从容器中删除这个视图。
    // 这是为了确保在finishUpdate(viewGroup)返回时视图能够被移除。
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    /**
     * 返回Tab的信息
     * @param position
     * @return
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
