package com.ioter.medical.bean;

public class WasteViewsBean extends BaseEntity{


    /**
     * Id : sample string 1
     * WasteTypeName : sample string 2
     * Weight : 3.0
     * Status : 初始
     * HandOverTime : 2019-11-13 11:45:27
     * ReceivedTime : 2019-11-13 11:45:27
     * DepartmentName : sample string 6
     */

    private String Id;
    private String WasteTypeName;
    private double Weight;
    private String Status;
    private String HandOverTime;
    private String ReceivedTime;
    private String DepartmentName;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getWasteTypeName() {
        return WasteTypeName;
    }

    public void setWasteTypeName(String WasteTypeName) {
        this.WasteTypeName = WasteTypeName;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double Weight) {
        this.Weight = Weight;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getHandOverTime() {
        return HandOverTime;
    }

    public void setHandOverTime(String HandOverTime) {
        this.HandOverTime = HandOverTime;
    }

    public String getReceivedTime() {
        return ReceivedTime;
    }

    public void setReceivedTime(String ReceivedTime) {
        this.ReceivedTime = ReceivedTime;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }
}
