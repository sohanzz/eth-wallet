package com.asifahmedsohan.ethwallet.model;


public class Transaction {
    private String from;
    private String nonce;

    public Transaction(String from, String nonce) {
        this.from = from;
        this.nonce = nonce;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
