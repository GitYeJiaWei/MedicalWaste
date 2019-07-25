package com.ioter.medical.bean;

public class EPC extends BaseEntity{
    String epc;
    String data1;
    String data2;
    String data3;
    String data4;
    int num;
    int overNum;
    int overTime;
    double money;

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getOverNum() {
        return overNum;
    }

    public void setOverNum(int overNum) {
        this.overNum = overNum;
    }

    public int getOverTime() {
        return overTime;
    }

    public void setOverTime(int overTime) {
        this.overTime = overTime;
    }



    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }
}
