package com.asifahmedsohan.ethwallet.viewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.asifahmedsohan.ethwallet.model.Transaction;
import com.asifahmedsohan.ethwallet.repository.WalletRepository;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private static final String TAG = "MainActivityViewModel";

    private MutableLiveData<List<Transaction>> transactionList;
    private BaseLiveData<String> balance;
    private BaseLiveData<String> nonce;

    private WalletRepository walletRepo;

    private Application application;
    private String apiKey;
    private String address;


    public MainActivityViewModel(Application application, String apiKey) {
        this.application = application;
        this.apiKey = apiKey;
    }

    public void updateData(String address) {
        this.address = address;
    }

    public void init() {
        if (transactionList != null) {
            return;
        }
        walletRepo = WalletRepository.getInstance(apiKey);
    }

    public LiveData<List<Transaction>> getTransactionList() {
        transactionList = walletRepo.getTransactions();
        return transactionList;
    }


    public BaseLiveData<String> getBalance() {
        balance = walletRepo.getBalance(address);
        return balance;
    }

    public BaseLiveData<String> getNonce() {
        nonce = walletRepo.getNonce(address);
        return balance;
    }
}
