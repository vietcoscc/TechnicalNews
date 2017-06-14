package com.example.vaio.technicalnews.model.application;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by vaio on 16/03/2017.
 */

public class GlobalData extends Application {
    private AccountManager accountManager;
    private ArrayList<String> arrAdmin;

    public ArrayList<String> getArrAdmin() {
        return arrAdmin;
    }

    public void setArrAdmin(ArrayList<String> arrAdmin) {
        this.arrAdmin = arrAdmin;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

}
