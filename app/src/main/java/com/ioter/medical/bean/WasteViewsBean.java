package com.ioter.medical.bean;

public class WasteViewsBean extends BaseEntity{

    /**
     * Id : sample string 1
     * WasteType : sample string 2
     * Weight : 3.0
     * Status : 待确认
     * CollectionTime : 2019-08-09 14:18:01
     * DepartmentName : sample string 5
     * HandOverUserName : sample string 6
     * CollectUserName : sample string 7
     * StockInTime : 2019-08-09 14:18:01
     */

    private String Id;
    private String WasteType;
    private double Weight;
    private String Status;
    private String CollectionTime;
    private String DepartmentName;
    private String HandOverUserName;
    private String CollectUserName;
    private String StockInTime;

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

    public String getCollectUserName() {
        return CollectUserName;
    }

    public void setCollectUserName(String CollectUserName) {
        this.CollectUserName = CollectUserName;
    }

    public String getStockInTime() {
        return StockInTime;
    }

    public void setStockInTime(String StockInTime) {
        this.StockInTime = StockInTime;
    }
}
