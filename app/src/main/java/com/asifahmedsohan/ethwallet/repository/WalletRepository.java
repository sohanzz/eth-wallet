package com.asifahmedsohan.ethwallet.repository;


import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.asifahmedsohan.ethwallet.R;
import com.asifahmedsohan.ethwallet.Util.BaseModel;
import com.asifahmedsohan.ethwallet.Util.LoadingStatus;
import com.asifahmedsohan.ethwallet.model.Transaction;
import com.asifahmedsohan.ethwallet.viewModel.BaseLiveData;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WalletRepository {

    private static final String TAG = "WalletRepository";

    private static WalletRepository instance;
    private ArrayList<Transaction> transactionDataList;

    private Web3j web3;

    public  static  WalletRepository getInstance(String apiKey) {
        if (instance == null) {
            instance = new WalletRepository(apiKey);
        }
        return instance;
    }

    public WalletRepository(String apiKey) {
        web3 = Web3j.build(new HttpService(apiKey));
    }

    public MutableLiveData<List<Transaction>> getTransactions() {
        MutableLiveData<List<Transaction>> mutableLiveData = new MutableLiveData<>();

        if (web3 != null) {
            try {
                transactionDataList = new ArrayList<>();

                /*Transactions api only work on Android 12
                * Due to some error on Big integer conversion from default library
                * Please try to use Android 12 Device
                * */

                List<EthBlock.TransactionResult> transactionResults = web3.ethGetBlockByNumber(DefaultBlockParameterName.PENDING, true).sendAsync().get().getBlock().getTransactions();
                transactionResults.forEach(tx -> {
                    EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                    transactionDataList.add(new Transaction(transaction.getFrom(), transaction.getNonceRaw()));

                });
                mutableLiveData.setValue(transactionDataList);
            } catch (Exception e) {
                Log.d(TAG, "retrieveTransactionDetails: " + e.getMessage());
            }
        }
        return mutableLiveData;
    }

    public BaseLiveData<String> getBalance(String address) {
        BaseLiveData<String> balance = new BaseLiveData<>();

        if (web3 != null) {
            try {
                EthGetBalance balanceWei = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();

                if (balanceWei.getError() != null) {
                    balance.setValue(new BaseModel<>(LoadingStatus.ERROR, "Invalid Address"));
                } else {
                    BigInteger balanceInWei = balanceWei.getBalance();
                    BigDecimal ethBalance = Convert.fromWei(balanceInWei.toString(), Convert.Unit.ETHER);

                    balance.setValue(new BaseModel<>(LoadingStatus.SUCCESSFUL, ethBalance.toString()));
                }
            } catch (Exception e) {
                balance.setValue(new BaseModel<>(LoadingStatus.EXCEPTION, e.getMessage()));
            }
        }
        return balance;
    }

    public BaseLiveData<String> getNonce(String address) {
        BaseLiveData<String> nonce = new BaseLiveData<>();

        if (web3 != null) {
            try {
                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                        address, DefaultBlockParameterName.LATEST).sendAsync().get();

                if (ethGetTransactionCount.getError() != null) {
                    nonce.setValue(new BaseModel<>(LoadingStatus.ERROR, "Invalid Address"));
                } else {
                    BigInteger nonceBigInt = ethGetTransactionCount.getTransactionCount();
                    Log.d(TAG, "retrieveNonce Nonce:" + nonce);
                    nonce.setValue(new BaseModel<>(LoadingStatus.SUCCESSFUL, nonceBigInt.toString()));
                }
            } catch (Exception e) {
                nonce.setValue(new BaseModel<>(LoadingStatus.EXCEPTION, e.getMessage()));
            }
        }
        return nonce;
    }
}
