package android_serialport_api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ioter.medical.ui.activity.EnterRegisterActivity;
import com.ioter.medical.ui.activity.MedicalRegisterActivity;
import com.ioter.medical.ui.activity.OutRegisterActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Created by Warwee on 2017/6/6.
 */

public class SerialPortUtil {

    public static SerialPort serialPort = null;
    public static InputStream inputStream = null;
    public static OutputStream outputStream = null;

    public static Thread receiveThread = null;

    public static boolean flag = false;

    public static String PORT = "/dev/ttyS3";
    public static int BAUDRATE = 19200;
    public static int status = 0;

    /**
     * 打开串口的方法
     */
    public static void openSrialPort(int _status) {
        status = _status;
        Log.i("test", "打开串口"+status);
        try {
            serialPort = new SerialPort(new File(PORT), BAUDRATE, 0);
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            flag = true;
            receiveSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭串口的方法
     * 关闭串口中的输入输出流
     * 然后将flag的值设为flag，终止接收数据线程
     */
    public static void closeSerialPort() {
        Log.i("test", "关闭串口");
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            flag = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送串口数据的方法
     *
     * @param sendData 要发送的数据
     */
    public static void sendSerialPort(byte[] sendData) {
        Log.i("test", "发送串口数据");
        try {
            outputStream.write(sendData);
            outputStream.flush();
            Log.i("test", "串口数据发送成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("test", "串口数据发送失败");
        }
    }

    /**
     * 接收串口数据的方法
     */
    public static void receiveSerialPort() {
        Log.i("test", "接收串口数据"+status);
        if (receiveThread != null)
            return;
        /*定义一个handler对象要来接收子线程中接收到的数据
            并调用Activity中的刷新界面的方法更新UI界面
         */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    double data = (double) msg.obj;
                    Log.i("test", "status"+status);
                    if (status ==1){
                        MedicalRegisterActivity.refreshTextView(data);
                    }else if (status == 2){
                        EnterRegisterActivity.refreshTextView(data);
                    }else if (status == 3){
                        OutRegisterActivity.refreshTextView(data);
                    }
                }
            }
        };
        /*创建子线程接收串口数据
         */
        receiveThread = new Thread() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        byte[] readData = new byte[1024];
                        if (inputStream == null) {
                            return;
                        }

                        int size = inputStream.read(readData);
                        if (size > 0 && flag) {
                            Log.i("test", "地址:" + readData[0]);
                            Log.i("test", "命令:" + readData[1]);
                            Log.i("test", "数值:" + readData[2]);
                            Log.i("test", "数值:" + readData[3]);
                            Log.i("test", "数值:" + readData[4]);
                            Log.i("test", "数值:" + readData[5]);
                            Log.i("test", "数值:" + readData[6]);
                            Log.i("test", "标志位:" + readData[7]);
                            Log.i("test", "校验码:" + readData[8]);
                            Log.i("test", "结束符:" + readData[9]);

                            int abx = ((0xff&readData[6])-0x30)*65536+((0xff&readData[5])-0x30)*4096+((0xff&readData[4])-0x30)*256+((0xff&readData[3])-0x30)*16+((0xff&readData[2])-0x30);
                            double a = division(abx,2000,3);
                            Log.i("test", "重量:" + a);
                            //将接收到的数据封装进Message中，然后发送给主线程

                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = a;
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //启动接收线程
        receiveThread.start();
    }


    public static double division(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
