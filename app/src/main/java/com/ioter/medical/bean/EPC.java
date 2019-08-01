package com.ioter.medical.bean;

public class EPC extends BaseEntity{
    String Id;
    String WasteType;
    String CollectionTime;
    String DepartmentName;
    String HandOverUserName;
    int Status;
    private double Weight;

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getWasteType() {
        return WasteType;
    }

    public void setWasteType(String wasteType) {
        WasteType = wasteType;
    }

    public String getCollectionTime() {
        return CollectionTime;
    }

    public void setCollectionTime(String collectionTime) {
        CollectionTime = collectionTime;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public String getHandOverUserName() {
        return HandOverUserName;
    }

    public void setHandOverUserName(String handOverUserName) {
        HandOverUserName = handOverUserName;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
