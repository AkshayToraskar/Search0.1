package com.ak.search.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by dg hdghfd on 29-11-2016.
 */

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "Search";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_USERNAME = "Username";
    private static final String KEY_TYPE = "Type";

    private static final String SURVEY_ID="SurveyId";



    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String Username, int Type) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_USERNAME,Username);
        editor.putInt(KEY_TYPE,Type);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public String getUsername(){
        return  pref.getString(KEY_USERNAME,"");
    }

    /*public boolean isAdmin(){
        return  pref.getBoolean(KEY_TYPE,false);
    }*/


    public int getLoginType()
    {
        return pref.getInt(KEY_TYPE,0);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }


    public void setSurveyId(int id)
    {   editor.putInt(SURVEY_ID,id);
        editor.commit();
    }

    public int getSurveyId(){
        return pref.getInt(SURVEY_ID,0);
    }


}
