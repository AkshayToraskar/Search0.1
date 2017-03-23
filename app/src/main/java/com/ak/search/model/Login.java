package com.ak.search.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dg hdghfd on 22-03-2017.
 */

public class Login {

    @SerializedName("error")
    boolean error;

    @SerializedName("error_msg")
    String error_message;

    @SerializedName("user")
    MUser user;


    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public MUser getUser() {
        return user;
    }

    public void setUser(MUser user) {
        this.user = user;
    }
}
