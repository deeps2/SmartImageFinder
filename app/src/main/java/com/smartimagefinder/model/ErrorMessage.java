package com.smartimagefinder.model;

public class ErrorMessage {
    private int offSet;
    private boolean isNetworkError;

    public int getOffSet() {
        return offSet;
    }

    public boolean isNetworkError() {
        return isNetworkError;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public void setNetworkError(boolean networkError) {
        isNetworkError = networkError;
    }
}
