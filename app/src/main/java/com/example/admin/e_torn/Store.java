package com.example.admin.e_torn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ausias on 01/03/17.
 */

public class Store implements Parcelable{
    private String id;
    private String name;
    private int storeTurn;
    private int usersTurn;
    private int queue;
    private int photo;

    public Store(String id, String name, int usersTurn, int storeTurn, int photo) {
        this.id = id;
        this.usersTurn = usersTurn;
        this.storeTurn = storeTurn;
        this.name = name;
        this.photo = photo;
        this.queue = usersTurn - storeTurn;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public void reloadQueue () {
        this.queue = this.usersTurn - this.storeTurn;
    }
    public Store (Parcel in) {
        this.name = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStoreTurn() {
        return storeTurn;
    }

    public void setStoreTurn(int storeTurn) {
        this.storeTurn = storeTurn;
    }

    public int getUsersTurn() {
        return usersTurn;
    }

    public void setUsersTurn(int usersTurn) {
        this.usersTurn = usersTurn;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel in) {
            return new Store(in);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };
}
