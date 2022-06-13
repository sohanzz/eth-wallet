package com.asifahmedsohan.ethwallet.Util;


public class BaseModel<T> {
    private LoadingStatus status;
    private T data;
    private Throwable error;

    public BaseModel(LoadingStatus status, T data, Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public BaseModel(LoadingStatus status, T data) {
        this(status, data, null);
    }

    public BaseModel(LoadingStatus status, Throwable error) {
        this(status, null, error);
    }

    public BaseModel(LoadingStatus status) {
        this(status, null, null);
    }

    public LoadingStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isSuccessful() {
        return LoadingStatus.SUCCESSFUL.equals(this.status);
    }

    public boolean isError() {
        return LoadingStatus.ERROR.equals(this.status);
    }
}
