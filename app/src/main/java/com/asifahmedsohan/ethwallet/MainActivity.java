package com.asifahmedsohan.ethwallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviderKt;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asifahmedsohan.ethwallet.Util.LoadingStatus;
import com.asifahmedsohan.ethwallet.Util.Utility;
import com.asifahmedsohan.ethwallet.adapter.TransactionAdapter;
import com.asifahmedsohan.ethwallet.model.Transaction;
import com.asifahmedsohan.ethwallet.viewModel.MainActivityViewModel;
import com.asifahmedsohan.ethwallet.viewModel.MainViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextInputEditText etAddress;
    private TextView tvBalance, tvNonce;
    private Button btnCheckBalance, btnNonce, btnTransactions;
    private ImageButton ibQrCode;

    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;

    private boolean isConnected;

    private IntentIntegrator intentIntegrator;
    private Web3j web3;

    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        initView();
        mainActivityViewModel = new ViewModelProvider(this, new MainViewModelFactory(this.getApplication(), getString(R.string.API_KEY))).get(MainActivityViewModel.class);
        mainActivityViewModel.init();

        checkNetworkConnection();
        connectToNetwork();
    }

    private void checkNetworkConnection() {
        isConnected = Utility.isInternetAvailable(this);

        if (!isConnected) {
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.internet_connection_alert))
                    .setMessage(getString(R.string.check_connection_msg))
                    .setPositiveButton(getString(R.string.close), (dialog, which) -> dialog.dismiss()).show();

            btnCheckBalance.setEnabled(false);
            btnTransactions.setEnabled(false);
            btnNonce.setEnabled(false);
        }
    }

    private void initView() {
        etAddress = findViewById(R.id.tiet_address);
        tvBalance = findViewById(R.id.tv_balance);
        btnCheckBalance = findViewById(R.id.btn_check_balance);
        btnNonce = findViewById(R.id.btn_nonce);
        btnTransactions = findViewById(R.id.btn_transactions);
        ibQrCode = findViewById(R.id.ib_qr_code);
        tvNonce = findViewById(R.id.tv_nonce);
        rvTransactions = findViewById(R.id.rv_transactions);

        bindRecyclerView();

        btnCheckBalance.setOnClickListener(this);
        ibQrCode.setOnClickListener(this);
        btnNonce.setOnClickListener(this);
        btnTransactions.setOnClickListener(this);

        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt(getString(R.string.qr_code_scan_text));
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setOrientationLocked(true);
    }

    private void bindRecyclerView() {
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setHasFixedSize(true);
        rvTransactions.setItemAnimator(new DefaultItemAnimator());

        transactionAdapter = new TransactionAdapter(this, new ArrayList<Transaction>());
        rvTransactions.setAdapter(transactionAdapter);
    }

    private void connectToNetwork() {
        //need to update with viewmodel
        if (isConnected) {
            web3 = Web3j.build(new HttpService(getString(R.string.API_KEY)));
            try {
                Web3ClientVersion clientVersion = web3.web3ClientVersion().sendAsync().get();
                if (!clientVersion.hasError()) {

                    mainActivityViewModel = new ViewModelProvider(this, new MainViewModelFactory(this.getApplication(), getString(R.string.API_KEY))).get(MainActivityViewModel.class);
                    mainActivityViewModel.init();

                    Log.d(TAG, "Connected Successfully");
                } else {
                    Log.d(TAG, clientVersion.getError().getMessage());
                    btnCheckBalance.setEnabled(false);
                    Toast.makeText(this, "Connection Error!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                btnCheckBalance.setEnabled(false);
                Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveBalance() {
        String address = etAddress.getText().toString().trim();

        if (Utility.isValid(address)) {
            mainActivityViewModel.updateData(address);
            mainActivityViewModel.getBalance().observe(this, balanceBaseModel -> {
                if (balanceBaseModel.isSuccessful()) {
                    tvBalance.setText(String.format("%s", balanceBaseModel.getData()));
                } else if (balanceBaseModel.isError()){
                    Toast.makeText(this, balanceBaseModel.getData(), Toast.LENGTH_SHORT).show();
                    tvBalance.setText(getString(R.string.balance_placeholder));
                } else {
                    tvBalance.setText(getString(R.string.balance_placeholder));
                    Log.d(TAG, "retrieveBalance:" + balanceBaseModel.getError());
                }
            });
        } else {
            tvBalance.setText(getString(R.string.balance_placeholder));
            etAddress.setError("Invalid address");
        }
    }

    private void retrieveNonce() {
        String address = etAddress.getText().toString().trim();

        if (Utility.isValid(address)) {
            mainActivityViewModel.updateData(address);
            mainActivityViewModel.getNonce().observe(this, nonceData -> {
                if (nonceData.isSuccessful()) {
                    tvNonce.setText(nonceData.getData());
                } else if (nonceData.isError()) {
                    Toast.makeText(this, "Invalid address.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "retrieveNonce" + nonceData.getError());
                }
            });
        } else {
            etAddress.setError(getString(R.string.invalid_address));
        }
    }

    private void retrieveTransactionDetails() {
        final String address = etAddress.getText().toString().trim().toLowerCase();

        if (Utility.isValid(address)) {
            mainActivityViewModel.getTransactionList().observe(this, transactions ->  {
                if (transactions != null && transactions.size() > 0)
                    transactionAdapter.updateData(transactions);
            });
        } else {
            etAddress.setError(getString(R.string.invalid_address));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnCheckBalance.getId()) {
            retrieveBalance();
        } else if (view.getId() == ibQrCode.getId()) {
            if (intentIntegrator != null) {
                intentIntegrator.initiateScan();
            }
        } else if (view.getId() == btnNonce.getId()) {
            retrieveNonce();
        } else if (view.getId() == btnTransactions.getId()) {
            retrieveTransactionDetails();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
            } else {
                etAddress.setError(null);
                etAddress.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}