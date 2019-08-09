package com.ioter.medical.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 医废统计
 */
public class CountFragment extends BaseFragment {
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_lease)
    Button btnLease;
    private BaseBean<FeeRule> baseBean;
    private List<Map<String, String>> dataList1;
    private SimpleAdapter adapter1;


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

        //设置基本概况
        initData1();
        String[] from1 = {"img", "text"};
        int[] to1 = {R.id.img, R.id.text};

        adapter1 = new SimpleAdapter(getContext(), dataList1, R.layout.gridview3_item, from1, to1);
        listLease.setAdapter(adapter1);
        listLease.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*for (int i = 0; i < dataList1.size(); i++) {
                    if (i == position) {
                        LinearLayout linearLayout = (LinearLayout) ((LinearLayout) listLease.getChildAt(i)).getChildAt(0);
                        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_back));
                    } else {
                        LinearLayout linearLayout = (LinearLayout) ((LinearLayout) listLease.getChildAt(i)).getChildAt(0);
                        linearLayout.setBackground(getResources().getDrawable(R.drawable.button_back1));
                    }
                }*/
            }
        });

    }

    private void initData1() {
        dataList1 = new ArrayList<>();
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean != null && baseBean.getData().getWasteStatistics() != null) {
            List<HashMap<String, Object>> mapList = (List<HashMap<String, Object>>) baseBean.getData().getWasteStatistics();
            for (int i = 0; i < mapList.size(); i++) {
                if (mapList.get(i).get("Id")==null){
                    continue;
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("img", mapList.get(i).get("Count") + "");
                map.put("text", mapList.get(i).get("WasteType") + "");
                dataList1.add(map);
            }
        }
    }

    @Override
    public void showBarCode(String barCode) {

    }

    @OnClick(R.id.btn_lease)
    public void onViewClicked() {
    }
}
