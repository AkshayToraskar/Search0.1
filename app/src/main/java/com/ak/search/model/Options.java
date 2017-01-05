package com.ak.search.model;


import org.parceler.Parcel;

import io.realm.OptionsRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dg hdghfd on 01-12-2016.
 */

@Parcel(implementations = { OptionsRealmProxy.class },
        value = Parcel.Serialization.FIELD,
        analyze = { Options.class })
public class Options extends RealmObject {

    @PrimaryKey
    long id;
    String opt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }
}
