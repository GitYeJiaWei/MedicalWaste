package com.ioter.medical.bean;

public class StockIn extends BaseEntity {

    /**
     * Id : sample string 1
     * DeliverCount : 2
     * DelivererName : sample string 3
     * DeliverTime : 2019-07-31 14:04:16
     * ReceiverName : sample string 5
     * DushbinEpc : sample string 6
     * DeliverWeight : 7.0
     * ReceivedWeight : 8.0
     * WasteViews : [{"Id":"sample string 1","WasteType":"sample string 2","Status":0,"CollectionTime":"2019-07-31 14:04:16","DepartmentName":"sample string 4","HandOverUserName":"sample string 5"},{"Id":"sample string 1","WasteType":"sample string 2","Status":0,"CollectionTime":"2019-07-31 14:04:16","DepartmentName":"sample string 4","HandOverUserName":"sample string 5"}]
     */

    private String Id;
    private int DeliverCount;
    private String DelivererName;
    private String DeliverTime;
    private String ReceiverName;
    private String DushbinEpc;
    private double DeliverWeight;
    private double ReceivedWeight;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public int getDeliverCount() {
        return DeliverCount;
    }

    public void setDeliverCount(int DeliverCount) {
        this.DeliverCount = DeliverCount;
    }

    public String getDelivererName() {
        return DelivererName;
    }

    public void setDelivererName(String DelivererName) {
        this.DelivererName = DelivererName;
    }

    public String getDeliverTime() {
        return DeliverTime;
    }

    public void setDeliverTime(String DeliverTime) {
        this.DeliverTime = DeliverTime;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String ReceiverName) {
        this.ReceiverName = ReceiverName;
    }

    public String getDushbinEpc() {
        return DushbinEpc;
    }

    public void setDushbinEpc(String DushbinEpc) {
        this.DushbinEpc = DushbinEpc;
    }

    public double getDeliverWeight() {
        return DeliverWeight;
    }

    public void setDeliverWeight(double DeliverWeight) {
        this.DeliverWeight = DeliverWeight;
    }

    public double getReceivedWeight() {
        return ReceivedWeight;
    }

    public void setReceivedWeight(double ReceivedWeight) {
        this.ReceivedWeight = ReceivedWeight;
    }

}
