package com.cybex.eos.event;

public class WalletIDEvent {

    public WalletIDEvent (Integer ID) {
        this.walletID = ID;
    }

    public Integer getWalletID() {
        return walletID;
    }

    private Integer walletID;

}
