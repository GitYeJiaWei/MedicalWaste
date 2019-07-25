package com.ioter.medical.common.rx;

import android.content.Context;
import android.widget.Toast;

import com.ioter.medical.common.exception.ApiException;
import com.ioter.medical.common.exception.BaseException;
import com.ioter.medical.common.exception.ErrorMessageFactory;

import java.net.SocketTimeoutException;


public class RxErrorHandler
{


    private Context mContext;

    public RxErrorHandler(Context context)
    {

        this.mContext = context;
    }

    public BaseException handleError(Throwable e)
    {

        BaseException exception = new BaseException();

        if (e instanceof SocketTimeoutException)
        {
            exception.setCode(BaseException.SOCKET_TIMEOUT_ERROR);
            exception.setDisplayMessage(ErrorMessageFactory.create(mContext, exception.getCode()));
        } else if (e instanceof ApiException)
        {
            exception.setCode(BaseException.API_ERROR);
            exception.setDisplayMessage(((ApiException) e).getDisplayMessage());
        } else
        {
            exception.setCode(BaseException.UNKNOWN_ERROR);
            exception.setDisplayMessage(ErrorMessageFactory.create(mContext, exception.getCode()));
        }

        return exception;
    }

    public void showErrorMessage(BaseException e)
    {
        Toast.makeText(mContext, e.getDisplayMessage(), Toast.LENGTH_LONG).show();
    }
}
