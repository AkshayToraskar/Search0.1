package com.ak.search.model;

import java.io.Serializable;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class Test1 implements Serializable {

    int id;

    public Test1(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
