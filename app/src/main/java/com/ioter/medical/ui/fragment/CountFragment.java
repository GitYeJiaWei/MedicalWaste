package com.ioter.medical.ui.fragment;

import android.content.Intent;
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
import com.ioter.medical.ui.activity.CheckMessageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 医废统计,统计的是当前月份的数据
 */
public class CountFragment extends BaseFragment {
    @BindView(R.id.list_lease)
    ListView listLease;
    @BindView(R.id.btn_lease)
    Button btnLease;
    private BaseBean<FeeRule> baseBean;
    private List<Map<String, String>> dataList1;
    private SimpleAdapter adapter1;
    private int mYear, mMonth;
    private String beginTime;

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
        initDate();
        //设置基本概况
        initData1();
        String[] from1 = {"img", "text"};
        int[] to1 = {R.id.img, R.id.text};

        adapter1 = new SimpleAdapter(getContext(), dataList1, R.layout.gridview3_item, from1, to1);
        listLease.setAdapter(adapter1);
        listLease.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AppApplication.getApplication(), CheckMessageActivity.class);
                intent.putExtra("WasteTypeId",dataList1.get(position).get("id"));
                intent.putExtra("Begin",beginTime);
                startActivity(intent);
            }
        });

    }

    private void initDate() {
        //初始化日期
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        String newDateStr = mYear + "-" + (mMonth + 1) + "-" + "01";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            Date date = sdf.parse(newDateStr);
            beginTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initData1() {
        dataList1 = new ArrayList<>();
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean != null && baseBean.getData().getWasteStatistics() != null) {
            List<HashMap<String, Object>> mapList = (List<HashMap<String, Object>>) baseBean.getData().getWasteStatistics();
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                if (mapList.get(i).get("Id")==null){
                    map.put("img", Math.round((double)mapList.get(i).get("Count"))+"");
                    map.put("text", mapList.get(i).get("WasteType") + "");
                    map.put("id",null);
                }else {
                    map.put("img", Math.round((double)mapList.get(i).get("Count"))+"");
                    map.put("text", mapList.get(i).get("WasteType") + "");
                    map.put("id",mapList.get(i).get("Id") + "");
                }
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
