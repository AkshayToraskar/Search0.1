package com.ak.search.realm_model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 28-07-2017.
 */

public class MyTreeNode extends RealmObject {

    @PrimaryKey
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
