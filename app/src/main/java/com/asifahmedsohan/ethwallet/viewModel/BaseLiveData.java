package com.asifahmedsohan.ethwallet.viewModel;

import androidx.lifecycle.LiveData;

import com.asifahmedsohan.ethwallet.Util.BaseModel;
import com.asifahmedsohan.ethwallet.Util.LoadingStatus;


public class BaseLiveData<T> extends LiveData<BaseModel<T>> {

    public BaseLiveData(T value) {
        super(new BaseModel<T>(LoadingStatus.SUCCESSFUL, value));
    }

    public BaseLiveData() {
        super();
    }

    @Override
    public void postValue(BaseModel<T> value) {
        super.postValue(value);
    }

    @Override
    public void setValue(BaseModel<T> value) {
        super.setValue(value);
    }
}
