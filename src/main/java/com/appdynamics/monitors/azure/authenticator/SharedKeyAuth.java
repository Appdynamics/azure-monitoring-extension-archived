package com.appdynamics.monitors.azure.authenticator;

public abstract class SharedKeyAuth {

    private HmacSHA256Sign signer;
    private String accountName;

    public SharedKeyAuth(String accountName, String accountKey) {
        this.accountName = accountName;
        signer = new HmacSHA256Sign(accountKey);
    }

    protected HmacSHA256Sign getSigner() {
        return signer;
    }

    protected String getAccountName() {
        return accountName;
    }
}
