package com.ioter.medical.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ioter.medical.R;
import com.ioter.medical.bean.StockIn;

import java.util.ArrayList;
import java.util.List;

public class MedicalEnterAdapter extends BaseAdapter {
    //定义需要包装的JSONArray对象
    public List<StockIn> mymodelList = new ArrayList<>();
    private Context context = null;
    private String size;
    //视图容器
    private LayoutInflater layoutInflater;

    public MedicalEnterAdapter(Context _context, String size) {
        this.context = _context;
        //创建视图容器并设置上下文
        this.layoutInflater = LayoutInflater.from(_context);
        this.size = size;
    }

    public void updateDatas(List<StockIn> datalist) {
        if (datalist == null) {
            return;
        } else {
            mymodelList.clear();
            mymodelList.addAll(datalist);
            notifyDataSetChanged();
        }
    }

    /**
     * 清空列表的所有数据
     */
    public void clearData() {
        mymodelList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.mymodelList.size();
    }

    @Override
    public Object getItem(int position) {
        if (getCount() > 0) {
            return this.mymodelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MedicalEnterAdapter.ListItemView listItemView = null;
        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.list_item_return, null);
            //获取控件对象
            listItemView = new MedicalEnterAdapter.ListItemView();
            listItemView.num = (TextView) convertView.findViewById(R.id.tv_num);
            listItemView.time = (TextView) convertView.findViewById(R.id.tv_time);
            listItemView.room = (TextView) convertView.findViewById(R.id.tv_room);
            listItemView.user = (TextView) convertView.findViewById(R.id.tv_user);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (MedicalEnterAdapter.ListItemView) convertView.getTag();
        }

        final StockIn m1 = (StockIn) this.getItem(position);
        listItemView.num.setText(m1.getId());
        listItemView.time.setText(m1.getDeliverCount());
        listItemView.room.setText(m1.getDeliverWeight()+"");
        listItemView.user.setText(m1.getDeliverTime());

        return convertView;
    }

    /**
     * 使用一个类来保存Item中的元素
     * 自定义控件集合
     */
    public final class ListItemView {
        TextView num, time, room, user;
    }
}