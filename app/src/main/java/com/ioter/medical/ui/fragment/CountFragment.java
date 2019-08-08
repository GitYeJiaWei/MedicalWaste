package com.ioter.medical.ui.fragment;

import android.view.View;

import com.ioter.medical.AppApplication;
import com.ioter.medical.R;
import com.ioter.medical.bean.BaseBean;
import com.ioter.medical.bean.FeeRule;
import com.ioter.medical.common.util.ACache;
import com.ioter.medical.di.component.AppComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医废统计
 */
public class CountFragment extends BaseFragment {
    private BaseBean<FeeRule> baseBean;

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
        baseBean = (BaseBean<FeeRule>) ACache.get(AppApplication.getApplication()).getAsObject("feeRule");
        if (baseBean != null && baseBean.getData().getWasteStatistics()!=null) {
            List<HashMap<String, Object>> mapList = (List<HashMap<String, Object>>) baseBean.getData().getWasteStatistics();
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("img", mapList.get(i).get("WasteType") + "");
                map.put("text", mapList.get(i).get("Count") + " kg");
            }
        }
    }

    @Override
    public void showBarCode(String barCode) {

    }

}
