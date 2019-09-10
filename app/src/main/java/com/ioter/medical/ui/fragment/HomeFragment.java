package com.ioter.medical.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.di.component.AppComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.gridview1)
    GridView gridview1;
    private BaseBean<FeeRule> baseBean;
    private List<Map<String, Object>> dataList;
    private List<Map<String, String>> dataList1;
    private MyAdapter adapter;
    private SimpleAdapter adapter1;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int setLayout() {
        return R.layout.home_layout;
    }

    @Override
    public void setupAcitivtyComponent(AppComponent appComponent) {

    }

    CallBackValue callBackValue;

    /**
     * fragment与activity产生关联是  回调这个方法
     */
    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        //当前fragment从activity重写了回调接口  得到接口的实例化对象
        callBackValue = (CallBackValue) getActivity();
    }

    private static class MyAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Map<String, Object>> dataList;
        public MyAdapter(Context context,List<Map<String, Object>> dataList){
            this.dataList = dataList;
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = layoutInflater.inflate(R.layout.gridview_item,null);
            ImageView iv = (ImageView) v.findViewById(R.id.img);
            TextView tv = (TextView) v.findViewById(R.id.text);
            String ip = ACache.get(AppApplication.getApplication()).getAsString("ip");
            String host = ACache.get(AppApplication.getApplication()).getAsString("host");
            Glide.with(AppApplication.getApplication()).load("http://" + ip + ":"+host+"/"+dataList.get(position).get("ImgUrl").toString())
                    .placeholder(R.mipmap.error)
                    .error(R.mipmap.error)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//关闭Glide的硬盘缓存机制
                    .into(iv);
            tv.setText(dataList.get(position).get("Name").toString());
            return v;
        }
    }

    private void initData() {
        dataList = new ArrayList<>();
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean != null && baseBean.getData().getMenus()!=null) {
            List<HashMap<String, Object>> mapList1 = (List<HashMap<String, Object>>) baseBean.getData().getMenus();
            for (int i = 0; i < mapList1.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("ImgUrl", mapList1.get(i).get("ImgUrl"));
                map.put("Name", mapList1.get(i).get("Name"));
                map.put("ActivityName",mapList1.get(i).get("ActivityName"));
                dataList.add(map);
            }
            adapter = new MyAdapter(getContext(), dataList);
            gridview.setAdapter(adapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    String strValue = dataList.get(arg2).get("ActivityName").toString();
                    callBackValue.SendMessageValue(strValue);
                }
            });
        }
    }

    private void initData1() {
        dataList1 = new ArrayList<>();
        if (baseBean != null && baseBean.getData().getWasteStatistics()!=null) {
            List<HashMap<String, Object>> mapList = (List<HashMap<String, Object>>) baseBean.getData().getWasteStatistics();
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("img", mapList.get(i).get("WasteType") + "");
                map.put("text", mapList.get(i).get("Weight") + " kg");
                dataList1.add(map);
            }
        }
    }

    @Override
    public void init(View view) {
        //设置医废管理
        initData();


        //设置基本概况
        initData1();
        String[] from1 = {"img", "text"};
        int[] to1 = {R.id.title_grid, R.id.text};

        adapter1 = new SimpleAdapter(getContext(), dataList1, R.layout.gridview1_item, from1, to1);
        gridview1.setAdapter(adapter1);
        gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < dataList1.size(); i++) {
                    if (i == position) {
                        LinearLayout linearLayout = (LinearLayout) ((LinearLayout) gridview1.getChildAt(i)).getChildAt(0);
                        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_back));
                    } else {
                        LinearLayout linearLayout = (LinearLayout) ((LinearLayout) gridview1.getChildAt(i)).getChildAt(0);
                        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_back1));
                    }
                }
            }
        });
    }

    @Override
    public void showBarCode(String barCode) {

    }


    //定义一个回调接口
    public interface CallBackValue {
        void SendMessageValue(String strValue);
    }

}
