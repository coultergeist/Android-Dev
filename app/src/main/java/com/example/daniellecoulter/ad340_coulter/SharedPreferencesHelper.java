package com.example.daniellecoulter.ad340_coulter;

import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private final SharedPreferences mSharedPreferences;
    static final String KEY_ENTRY = "text_entry";

    public SharedPreferencesHelper(SharedPreferences sharedPreferences){
        mSharedPreferences = sharedPreferences;
    }

    public boolean putSharedPreferencesHelper(String str){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_ENTRY, str);

        return editor.commit();
    }

    public String getSharedPreferences(){
        String str = mSharedPreferences.getString(KEY_ENTRY, "");
        return str;
    }
}
