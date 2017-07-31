package com.ak.search.realm_model;

import io.realm.RealmObject;

/**
 * Created by dg hdghfd on 31-07-2017.
 */

public class TreeString extends RealmObject {
    String treeData;

    public String getTreeData() {
        return treeData;
    }

    public void setTreeData(String treeData) {
        this.treeData = treeData;
    }
}
