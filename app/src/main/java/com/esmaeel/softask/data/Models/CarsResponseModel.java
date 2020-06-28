package com.esmaeel.softask.data.Models;

import java.util.ArrayList;

public class CarsResponseModel {

    private Integer status;
    private ArrayList<DataBean> data;
    private ErrorBean error;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public static class ErrorBean {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public int getStatus() {
        return status;
    }

    public Boolean isSuccessful() {
        return status != null && status.equals(1);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int id;
        private String brand;
        private String constructionYear;
        private Boolean isUsed;
        private String imageUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBrand() {
            return brand == null ? "" : brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getConstructionYear() {
            return constructionYear == null ? "" : constructionYear;
        }

        public void setConstructionYear(String constructionYear) {
            this.constructionYear = constructionYear;
        }

        public boolean isIsUsed() {
            return isUsed;
        }

        public String getUsedStatus() {
            if (isUsed != null) {
                if (isUsed)
                    return "Used";
                else return "New";
            } else return "";
        }

        public void setIsUsed(boolean isUsed) {
            this.isUsed = isUsed;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
