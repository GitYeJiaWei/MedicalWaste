package com.ioter.medical.bean;


public class FeeRule extends BaseEntity {
    /**
     * WasteStatistics : {}
     * Menus : []
     */

    private Object WasteStatistics;
    private Object Menus;

    public Object getWasteStatistics() {
        return WasteStatistics;
    }

    public void setWasteStatistics(Object WasteStatistics) {
        this.WasteStatistics = WasteStatistics;
    }

    public Object getMenus() {
        return Menus;
    }

    public void setMenus(Object menus) {
        Menus = menus;
    }
}
