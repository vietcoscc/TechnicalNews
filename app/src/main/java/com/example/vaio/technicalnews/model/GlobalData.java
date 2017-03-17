package com.example.vaio.technicalnews.model;

import android.app.Application;

/**
 * Created by vaio on 16/03/2017.
 */

public class GlobalData extends Application {
    private AccountManager accountManager;

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }
}
