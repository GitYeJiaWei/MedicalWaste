package com.ioter.medical.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioter.medical.R;
import com.ioter.medical.bean.Remind;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.ui.activity.RemindActivity;

import java.util.ArrayList;
import java.util.List;

public class RemindMessageAdapter extends BaseAdapter {
    //定义需要包装的JSONArray对象
    public List<Remind> mymodelList = new ArrayList<>();
    private RemindActivity context = null;
    private String size;
    //视图容器
    private LayoutInflater layoutInflater;

    public RemindMessageAdapter(RemindActivity _context, String size) {
        this.context = _context;
        //创建视图容器并设置上下文
        this.layoutInflater = LayoutInflater.from(_context);
        this.size = size;
    }

    public void updateDatas(List<Remind> datalist) {
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
        RemindMessageAdapter.ListItemView listItemView = null;
        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.remind_item, null);
            //获取控件对象
            listItemView = new RemindMessageAdapter.ListItemView();
            listItemView.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            listItemView.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            listItemView.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            listItemView.img_remind = (ImageView) convertView.findViewById(R.id.img_remind);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (RemindMessageAdapter.ListItemView) convertView.getTag();
        }

        final Remind m1 = (Remind) this.getItem(position);
        listItemView.tv_title.setText(m1.getTitle());
        listItemView.tv_time.setText(m1.getCreateTime());
        listItemView.tv_content.setText(m1.getContent());
        if (m1.getStatus()==0){
            listItemView.img_remind.setBackgroundResource(R.mipmap.unreadmind);
        }else if (m1.getStatus() == 1){
            listItemView.img_remind.setBackgroundResource(R.mipmap.readmind);
        }

        return convertView;
    }

    /**
     * 使用一个类来保存Item中的元素
     * 自定义控件集合
     */
    public final class ListItemView {
        TextView tv_title, tv_time, tv_content;
        ImageView img_remind;
    }
}