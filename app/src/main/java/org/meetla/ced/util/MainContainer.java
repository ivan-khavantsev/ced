package org.meetla.ced.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public final class MainContainer {

    private final static MainContainer instance = new MainContainer();

    private MainContainer() {
    }

    public static MainContainer getInstance() {
        return instance;
    }

    public String getMnemonic(Context context) {
        SharedPreferences preferences = null;
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            preferences = EncryptedSharedPreferences.create("security", masterKey, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (Throwable t) {
            System.out.println("ERROR ENC PREFS");
        }
        String mnemonic;
        if (preferences.contains("mnemonic")) {
            mnemonic = preferences.getString("mnemonic", null);
        } else {
            mnemonic = "cotton affair deliver situate pact bitter pool box cloth car soon budget equal innocent where hour toss oblige coil kick bottom face guess slush";
            preferences.edit().putString("mnemonic", mnemonic).apply();
        }
        return mnemonic;
    }
}
