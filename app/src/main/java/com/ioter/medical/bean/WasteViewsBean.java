package com.ioter.medical.bean;

public class WasteViewsBean extends BaseEntity{
    /**
     * Id : sample string 1
     * WasteType : sample string 2
     * Status : 0
     * CollectionTime : 2019-07-31 14:04:16
     * DepartmentName : sample string 4
     * HandOverUserName : sample string 5
     */

    private String Id;
    private String WasteType;
    private int Status;
    private double Weight;
    private String CollectionTime;
    private String DepartmentName;
    private String HandOverUserName;

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getWasteType() {
        return WasteType;
    }

    public void setWasteType(String WasteType) {
        this.WasteType = WasteType;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getCollectionTime() {
        return CollectionTime;
    }

    public void setCollectionTime(String CollectionTime) {
        this.CollectionTime = CollectionTime;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public String getHandOverUserName() {
        return HandOverUserName;
    }

    public void setHandOverUserName(String HandOverUserName) {
        this.HandOverUserName = HandOverUserName;
    }
}
