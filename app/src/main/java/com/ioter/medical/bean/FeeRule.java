package com.ioter.medical.bean;

import java.util.List;

public class FeeRule extends BaseEntity {
        /**
         * WasteStatistics : {}
         * Buttons : []
         */

        private Object WasteStatistics;
        private List<String> Buttons;

        public Object getWasteStatistics() {
            return WasteStatistics;
        }

        public void setWasteStatistics(Object WasteStatistics) {
            this.WasteStatistics = WasteStatistics;
        }

        public List<String> getButtons() {
            return Buttons;
        }

        public void setButtons(List<String> Buttons) {
            this.Buttons = Buttons;
        }


}
