package com.gzsll.hupu.ui.splash;



/**
 */
public class DataInfo {


    /**
     * data : {"app_status":"1","app_url":"https://www.agc98.vip/mobile/#/home","extend_1_title":"","extend_1_status":"0","extend_1_url":"","extend_2_title":"","extend_2_status":"0","extend_2_url":""}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * app_status : 1
         * app_url : https://www.agc98.vip/mobile/#/home
         * extend_1_title :
         * extend_1_status : 0
         * extend_1_url :
         * extend_2_title :
         * extend_2_status : 0
         * extend_2_url :
         */

        private String app_status;
        private String app_url;
        private String extend_1_title;
        private String extend_1_status;
        private String extend_1_url;
        private String extend_2_title;
        private String extend_2_status;
        private String extend_2_url;

        public String getApp_status() {
            return app_status;
        }

        public void setApp_status(String app_status) {
            this.app_status = app_status;
        }

        public String getApp_url() {
            return app_url;
        }

        public void setApp_url(String app_url) {
            this.app_url = app_url;
        }

        public String getExtend_1_title() {
            return extend_1_title;
        }

        public void setExtend_1_title(String extend_1_title) {
            this.extend_1_title = extend_1_title;
        }

        public String getExtend_1_status() {
            return extend_1_status;
        }

        public void setExtend_1_status(String extend_1_status) {
            this.extend_1_status = extend_1_status;
        }

        public String getExtend_1_url() {
            return extend_1_url;
        }

        public void setExtend_1_url(String extend_1_url) {
            this.extend_1_url = extend_1_url;
        }

        public String getExtend_2_title() {
            return extend_2_title;
        }

        public void setExtend_2_title(String extend_2_title) {
            this.extend_2_title = extend_2_title;
        }

        public String getExtend_2_status() {
            return extend_2_status;
        }

        public void setExtend_2_status(String extend_2_status) {
            this.extend_2_status = extend_2_status;
        }

        public String getExtend_2_url() {
            return extend_2_url;
        }

        public void setExtend_2_url(String extend_2_url) {
            this.extend_2_url = extend_2_url;
        }
    }
}
