package com.example.vaio.technicalnews.model;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by vaio on 16/03/2017.
 */

public class GlobalData extends Application {
    private AccountManager accountManager;
    private ArrayList<String> arrAdmin;
    private ArrayList<String> arrBan;

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

    public ArrayList<String> getArrBan() {
        return arrBan;
    }

    public void setArrBan(ArrayList<String> arrBan) {
        this.arrBan = arrBan;
    }
}
