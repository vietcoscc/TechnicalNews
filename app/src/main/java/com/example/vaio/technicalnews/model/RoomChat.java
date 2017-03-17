package com.example.vaio.technicalnews.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vaio on 15/03/2017.
 */

public class RoomChat implements Serializable {
    private String area;
    private int onlineNumber;
    private ArrayList<ItemChat> arrChat;

    public RoomChat() {
    }

    public RoomChat(String area, int onlineNumber, ArrayList<ItemChat> arrChat) {
        this.area = area;
        this.onlineNumber = onlineNumber;
        this.arrChat = arrChat;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getOnlineNumber() {
        return onlineNumber;
    }

    public void setOnlineNumber(int onlineNumber) {
        this.onlineNumber = onlineNumber;
    }

    public ArrayList<ItemChat> getArrChat() {
        return arrChat;
    }

    public void setArrChat(ArrayList<ItemChat> arrChat) {
        this.arrChat = arrChat;
    }
}
