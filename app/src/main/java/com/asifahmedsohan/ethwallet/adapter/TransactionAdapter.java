package com.asifahmedsohan.ethwallet.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.asifahmedsohan.ethwallet.R;
import com.asifahmedsohan.ethwallet.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    private List<Transaction> transactions;
    private Context mContext;


    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.mContext = context;
        this.transactions = transactions;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (transactions != null && transactions.get(position) != null) {
            Transaction item = transactions.get(position);
            holder.tvPublicKey.setText(String.format("%s %s", "From:", item.getFrom()));
            holder.tvNonce.setText(String.format("%s %s", "Nonce:", item.getNonce()));
        }
    }

    public void updateData(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPublicKey;
        private TextView tvNonce;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPublicKey = itemView.findViewById(R.id.tv_public_key);
            tvNonce = itemView.findViewById(R.id.tv_nonce);
        }
    }
}
