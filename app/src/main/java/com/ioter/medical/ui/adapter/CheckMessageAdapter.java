package com.ioter.medical.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.WasteViewsBean;
import com.ioter.medical.ui.activity.CheckMessageActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckMessageAdapter extends BaseAdapter {
    //定义需要包装的JSONArray对象
    public List<WasteViewsBean> mymodelList = new ArrayList<>();
    private CheckMessageActivity context = null;
    private String size;
    //视图容器
    private LayoutInflater layoutInflater;

    public CheckMessageAdapter(CheckMessageActivity _context, String size) {
        this.context = _context;
        //创建视图容器并设置上下文
        this.layoutInflater = LayoutInflater.from(_context);
        this.size = size;
    }

    public void updateDatas(List<WasteViewsBean> datalist) {
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
        CheckMessageAdapter.ListItemView listItemView = null;
        if (convertView == null) {
            //获取list_item布局文件的视图
            convertView = layoutInflater.inflate(R.layout.check_item, null);
            //获取控件对象
            listItemView = new CheckMessageAdapter.ListItemView();
            listItemView.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            listItemView.tv_room = (TextView) convertView.findViewById(R.id.tv_room);
            listItemView.tv_weight = (TextView) convertView.findViewById(R.id.tv_weight);
            listItemView.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            listItemView.tv_startTime = (TextView) convertView.findViewById(R.id.tv_startTime);
            listItemView.tv_endTime = (TextView) convertView.findViewById(R.id.tv_endTime);
            listItemView.tv_user = (TextView) convertView.findViewById(R.id.tv_user);
            listItemView.img_qr = (ImageView) convertView.findViewById(R.id.img_qr);
            listItemView.img_sure = (ImageView) convertView.findViewById(R.id.img_sure);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (CheckMessageAdapter.ListItemView) convertView.getTag();
        }

        final WasteViewsBean m1 = (WasteViewsBean) this.getItem(position);
        listItemView.tv_type.setText(m1.getWasteType());
        listItemView.tv_room.setText(m1.getDepartmentName());
        listItemView.tv_weight.setText(m1.getWeight()+"");
        listItemView.tv_name.setText(m1.getHandOverUserName());
        listItemView.tv_startTime.setText(m1.getCollectionTime());
        listItemView.tv_endTime.setText(m1.getStockInTime());
        listItemView.tv_user.setText(m1.getCollectUserName());

        final ListItemView finalListItemView = listItemView;
        AppApplication.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                context.Create2QR2("{iotEPC:"+m1.getId()+"}", finalListItemView.img_qr);
            }
        });


        if (m1.getStatus().equals("待确认")) {
            listItemView.img_sure.setBackgroundResource(R.mipmap.sure_wait);
        }else if (m1.getStatus().equals("已确认")) {
            listItemView.img_sure.setBackgroundResource(R.mipmap.sure_set);
        }else if (m1.getStatus().equals("待入库")) {
            listItemView.img_sure.setBackgroundResource(R.mipmap.in_wait);
        }else if (m1.getStatus().equals("已入库")) {
            listItemView.img_sure.setBackgroundResource(R.mipmap.in_set);
        }else if (m1.getStatus().equals("待出库")) {
            listItemView.img_sure.setBackgroundResource(R.mipmap.out_wait);
        }else if (m1.getStatus().equals("已出库")) {
            listItemView.img_sure.setBackgroundResource(R.mipmap.out_set);
        }
        return convertView;
    }

    /**
     * 使用一个类来保存Item中的元素
     * 自定义控件集合
     */
    public final class ListItemView {
        TextView tv_type, tv_room, tv_weight,tv_name,tv_startTime,tv_endTime,
                tv_user;
        ImageView img_qr,img_sure;
    }
}