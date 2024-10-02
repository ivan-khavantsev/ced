package org.meetla.ced.util;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public final class MainContainer {

    private MainContainer() {
    }

    private Context context;
    private ClipboardManager clipboardManager;


    private final static MainContainer instance = new MainContainer();

    public static MainContainer getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ClipboardManager getClipboardManager() {
        return clipboardManager;
    }

    public void setClipboardManager(ClipboardManager clipboardManager) {
        this.clipboardManager = clipboardManager;
    }

    public String getMnemonic() {
        SharedPreferences preferences = null;
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            preferences = EncryptedSharedPreferences.create("security", masterKey, this.context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
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
