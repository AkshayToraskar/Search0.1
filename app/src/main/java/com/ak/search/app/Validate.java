package com.ak.search.app;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class Validate {

    public boolean validateString(String name) {
        if (name.equals(""))
            return true;
        else
            return false;
    }

    public boolean validateRB(int id) {
        if (id == -1)
            return true;
        else
            return false;
    }

}
