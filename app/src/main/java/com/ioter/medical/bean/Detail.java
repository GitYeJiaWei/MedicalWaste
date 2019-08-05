package com.ioter.medical.bean;

import java.util.List;

public class Detail extends BaseEntity {

    /**
     * Id : sample string 1
     * DeliverCount : 2
     * DelivererName : sample string 3
     * DeliverTime : 2019-08-02 09:26:22
     * ReceiverName : sample string 5
     * DushbinEpc : sample string 6
     * DeliverWeight : 7.0
     * ReceivedWeight : 8.0
     * WasteViews : [{"Id":"sample string 1","WasteType":"sample string 2","Weight":3,"Status":0,"CollectionTime":"2019-08-02 09:26:22","DepartmentName":"sample string 5","HandOverUserName":"sample string 6"},{"Id":"sample string 1","WasteType":"sample string 2","Weight":3,"Status":0,"CollectionTime":"2019-08-02 09:26:22","DepartmentName":"sample string 5","HandOverUserName":"sample string 6"}]
     */

    private String Id;
    private int DeliverCount;
    private String DelivererName;
    private String DeliverTime;
    private String ReceiverName;
    private String DushbinEpc;
    private double DeliverWeight;
    private double ReceivedWeight;
    private List<WasteViewsBean> WasteViews;

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

    public List<WasteViewsBean> getWasteViews() {
        return WasteViews;
    }

    public void setWasteViews(List<WasteViewsBean> WasteViews) {
        this.WasteViews = WasteViews;
    }

    public static class WasteViewsBean {
        /**
         * Id : sample string 1
         * WasteType : sample string 2
         * Weight : 3.0
         * Status : 0
         * CollectionTime : 2019-08-02 09:26:22
         * DepartmentName : sample string 5
         * HandOverUserName : sample string 6
         */

        private String Id;
        private String WasteType;
        private double Weight;
        private int Status;
        private String CollectionTime;
        private String DepartmentName;
        private String HandOverUserName;

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
}
