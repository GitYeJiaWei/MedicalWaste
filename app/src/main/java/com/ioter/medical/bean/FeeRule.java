package com.ioter.medical.bean;


public class FeeRule extends BaseEntity {
    /**
     * WasteStatistics : {}
     * Menus : []
     */

    private Object WasteStatistics;
    private Object Menus;
    /**
     * AutoUpdateInfo : {"Version":"sample string 1","UpdateInfo":"sample string 2","FilePath":"sample string 3"}
     */

    private AutoUpdateInfoBean AutoUpdateInfo;


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

    public AutoUpdateInfoBean getAutoUpdateInfo() {
        return AutoUpdateInfo;
    }

    public void setAutoUpdateInfo(AutoUpdateInfoBean AutoUpdateInfo) {
        this.AutoUpdateInfo = AutoUpdateInfo;
    }

    public static class AutoUpdateInfoBean extends BaseEntity{
        /**
         * Version : sample string 1
         * UpdateInfo : sample string 2
         * FilePath : sample string 3
         */

        private String Version;
        private String UpdateInfo;
        private String FilePath;

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public String getUpdateInfo() {
            return UpdateInfo;
        }

        public void setUpdateInfo(String UpdateInfo) {
            this.UpdateInfo = UpdateInfo;
        }

        public String getFilePath() {
            return FilePath;
        }

        public void setFilePath(String FilePath) {
            this.FilePath = FilePath;
        }
    }
}
