package com.ioter.medical.common.rx.subscriber;

import android.content.Context;

import com.ioter.medical.common.exception.BaseException;
import com.ioter.medical.common.util.ToastUtil;
import com.ioter.medical.ui.BaseView;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.HttpException;

import java.io.IOException;

public abstract class ProgressSubcriber<T> extends ErrorHandlerSubscriber<T> {


    private BaseView mView;


    public ProgressSubcriber(Context context, BaseView view) {
        super(context);
        this.mView = view;

    }


    public boolean isShowProgress() {
        return true;
    }


    @Override
    public void onSubscribe(Disposable d) {
        if (isShowProgress()) {
            mView.showLoading();
        }
    }

    @Override
    public void onComplete() {
        mView.dismissLoading();
    }

    @Override
    public void onError(Throwable e) {
        mView.dismissLoading();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ResponseBody responseBody = httpException.response().errorBody();
            if (responseBody != null) {
                /*//重点在这：经测试表明，responseBody.string()的值只能获取一次，获取一次之后再获取都为空。
                //错误的做法
                LogUtil.d(TAG, responseBody.string() + "1");//第一次获取有值，没问题。
                LogUtil.d(TAG, responseBody.string() + "2");//这里开始responseBody.string()获取到的值为空。
                JSONObject jsonObject = new JSONObject(responseBody.string());//这里就会得到一个空的jsonObject
                String status = (String) jsonObject.get("status");*/
                //正确的做法
                try {
                    String json = responseBody.string();//第一次获取就保存下来
                    JSONObject jsonObject = new JSONObject(json);
                    String message = (String) jsonObject.get("message");

                    mView.showError(message);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }else {
            ToastUtil.toast(e.getMessage());
        }
    }

}
