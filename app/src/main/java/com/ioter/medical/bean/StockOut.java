package com.ioter.medical.bean;

import java.util.List;

public class StockOut extends BaseEntity {

    /**
     * Id : sample string 1
     * DelivererName : sample string 2
     * DeliverTime : 2019-08-06 17:49:44
     * DeliverWeight : 4.0
     * DeliverCount : 5
     * ReceiverName : sample string 6
     * ReceivedWeight : 7.0
     * DushbinViews : [{"Epc":"sample string 1","Weight":2,"WasteType":"sample string 3"},{"Epc":"sample string 1","Weight":2,"WasteType":"sample string 3"}]
     */

    private String Id;
    private String DelivererName;
    private String DeliverTime;
    private double DeliverWeight;
    private int DeliverCount;
    private String ReceiverName;
    private double ReceivedWeight;
    private List<DushbinViewsBean> DushbinViews;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
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

    public double getDeliverWeight() {
        return DeliverWeight;
    }

    public void setDeliverWeight(double DeliverWeight) {
        this.DeliverWeight = DeliverWeight;
    }

    public int getDeliverCount() {
        return DeliverCount;
    }

    public void setDeliverCount(int DeliverCount) {
        this.DeliverCount = DeliverCount;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String ReceiverName) {
        this.ReceiverName = ReceiverName;
    }

    public double getReceivedWeight() {
        return ReceivedWeight;
    }

    public void setReceivedWeight(double ReceivedWeight) {
        this.ReceivedWeight = ReceivedWeight;
    }

    public List<DushbinViewsBean> getDushbinViews() {
        return DushbinViews;
    }

    public void setDushbinViews(List<DushbinViewsBean> DushbinViews) {
        this.DushbinViews = DushbinViews;
    }

    public static class DushbinViewsBean {
        /**
         * Epc : sample string 1
         * Weight : 2.0
         * WasteType : sample string 3
         */

        private String Epc;
        private double Weight;
        private String WasteType;

        public String getEpc() {
            return Epc;
        }

        public void setEpc(String Epc) {
            this.Epc = Epc;
        }

        public double getWeight() {
            return Weight;
        }

        public void setWeight(double Weight) {
            this.Weight = Weight;
        }

        public String getWasteType() {
            return WasteType;
        }

        public void setWasteType(String WasteType) {
            this.WasteType = WasteType;
        }
    }
}
