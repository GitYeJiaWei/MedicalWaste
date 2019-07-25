package com.ioter.medical.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.di.component.AppComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.gridview1)
    GridView gridview1;
    private BaseBean<String> baseBean;
    private List<Map<String, Object>> dataList;
    private List<Map<String, Object>> dataList1;
    private SimpleAdapter adapter;
    private SimpleAdapter adapter1;
    CallBackValue callBackValue;

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

    private void initData() {
        dataList = new ArrayList<Map<String, Object>>();
        //baseBean = (BaseBean<String>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean == null) {
            for (int i = 0; i < 2; i++) {
                String id = i + 1 + "";
                Map<String, Object> map = new HashMap<String, Object>();
                if (id.equals("1")) {
                    map.put("img", R.mipmap.main01);
                    map.put("text", "医废收集");
                } else if (id.equals("2")) {
                    map.put("img", R.mipmap.main05);
                    map.put("text", "医废入库");
                }
                dataList.add(map);
            }
        }
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (i == 0) {
                map.put("img", R.mipmap.main08);
                map.put("text", "医废查询");
            }else {
                map.put("img", R.mipmap.main10);
                map.put("text", "设置");
            }
            dataList.add(map);
        }
    }

    private void initData1() {
        dataList1 = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (i == 0) {
                map.put("img", "垃圾总量");
                map.put("text", "458.6g");
            } else {
                map.put("img", "感染性垃圾");
                map.put("text", "121.5g");
            }
            dataList1.add(map);
        }
    }

    @Override
    public void init(View view) {
        //设置医废管理
        initData();
        String[] from = {"img", "text"};
        int[] to = {R.id.img, R.id.text};

        adapter = new SimpleAdapter(getContext(), dataList, R.layout.gridview_item, from, to);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String strValue = dataList.get(arg2).get("text").toString();
                callBackValue.SendMessageValue(strValue);
            }
        });

        //设置基本概况
        initData1();
        String[] from1 = {"img", "text"};
        int[] to1 = {R.id.title_grid, R.id.text};

        adapter1 = new SimpleAdapter(getContext(), dataList1, R.layout.gridview1_item, from1, to1);
        gridview1.setAdapter(adapter1);
    }

    @Override
    public void setBarCode(String barCode) {

    }

    //定义一个回调接口
    public interface CallBackValue {
        public void SendMessageValue(String strValue);
    }

}
