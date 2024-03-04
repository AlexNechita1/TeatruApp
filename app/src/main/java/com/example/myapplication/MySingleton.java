package com.example.myapplication;

public class MySingleton {
    private static MySingleton instance;
    private Database myObject;

    private MySingleton() {

    }

    public static synchronized MySingleton getInstance() {
        if (instance == null) {
            instance = new MySingleton();
        }
        return instance;
    }

    public Database getMyObject() {
        return myObject;
    }

    public void setMyObject(Database myObject) {
        this.myObject = myObject;
    }
}