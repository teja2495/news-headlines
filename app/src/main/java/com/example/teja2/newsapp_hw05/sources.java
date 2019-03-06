package com.example.teja2.newsapp_hw05;

import java.io.Serializable;

/* HW05 - Group 32
Created by
Bala Guna Teja Karlapudi
*/
public class sources implements Serializable {
    String name, id;

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
}
