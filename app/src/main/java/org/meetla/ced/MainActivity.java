package org.meetla.ced;

import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import org.meetla.ced.databinding.ActivityMainBinding;
import org.meetla.ced.model.Message;
import org.meetla.ced.util.Cryptography;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        EditText textEdit = findViewById(R.id.editTextTextMultiLine);

        SharedPreferences preferences = null;
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            preferences = EncryptedSharedPreferences.create("security", masterKey, this, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
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

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        Button buttonClear = findViewById(R.id.clearButton);
        buttonClear.setOnClickListener(i -> {
            textEdit.setText("");
        });

        Button buttonEnc = findViewById(R.id.button2);
        buttonEnc.setOnClickListener(i -> {
            try {
                //String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair1.getPublic().getEncoded());
                String publicKeyBase64 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE4tODL6kkdfcB4ZbpaeC3h0e4+C/LqboPM6W9M2LpKg+Va8NCfMoBPBbvd0LMdfW2BbiR7TkG7ZntZ1pfmMExlw==";
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
                PublicKey publicKey = Cryptography.loadPublicKey(publicKeyBytes);
                Message message = Cryptography.encrypt(textEdit.getText().toString().getBytes(), publicKey);
                clipboard.setText(message.encode());
                Toast.makeText(getApplicationContext(), "Encrypted and Copied!", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonDec = findViewById(R.id.button);
        buttonDec.setOnClickListener(i -> {
            try {
                String clipboardData = clipboard.getText().toString();
                if (clipboard.getText() != null) {
                    Message message = Message.decode(clipboardData);
                    KeyPair keyPair = Cryptography.loadKeyPairFromMnemonic(mnemonic);
                    textEdit.setText(new String(Cryptography.decrypt(message, keyPair.getPrivate())));
                } else {
                    Toast.makeText(getApplicationContext(), "Empty clipboard", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "Decrypted", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}