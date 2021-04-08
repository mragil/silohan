package com.example.silohan.Model;

import java.io.Serializable;

public class User {
    private String userID;
    private String namaLengkap;
    private String email;
    private String password;
    private int level;

    public User (){}

    public User(String userID,String namaLengkap, String email, String password, int level) {
        this.userID = userID;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.password = password;
        this.level = level;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
