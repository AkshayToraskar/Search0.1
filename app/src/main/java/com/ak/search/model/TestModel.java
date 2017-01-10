package com.ak.search.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dg hdghfd on 10-01-2017.
 */

public class TestModel implements Serializable{

    String name;
    List<Test1> test1List;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Test1> getTest1List() {
        return test1List;
    }

    public void setTest1List(List<Test1> test1List) {
        this.test1List = test1List;
    }
}
