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
import org.meetla.ced.util.MainContainer;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainContainer.getInstance().setContext(this);
        MainContainer.getInstance().setClipboardManager((ClipboardManager) getSystemService(CLIPBOARD_SERVICE));

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

        Button buttonClear = findViewById(R.id.clearButton);
        buttonClear.setOnClickListener(i -> {
            textEdit.setText("");
        });

        Button buttonEnc = findViewById(R.id.encryptButton);
        buttonEnc.setOnClickListener(i -> {
            try {
                //String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair1.getPublic().getEncoded());
                String publicKeyBase64 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE4tODL6kkdfcB4ZbpaeC3h0e4+C/LqboPM6W9M2LpKg+Va8NCfMoBPBbvd0LMdfW2BbiR7TkG7ZntZ1pfmMExlw==";
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
                PublicKey publicKey = Cryptography.loadPublicKey(publicKeyBytes);
                Message message = Cryptography.encrypt(textEdit.getText().toString().getBytes(), publicKey);
                MainContainer.getInstance().getClipboardManager().setText(message.encode());
                Toast.makeText(getApplicationContext(), "Encrypted and Copied!", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonDec = findViewById(R.id.decryptButton);
        buttonDec.setOnClickListener(i -> {
            try {
                if (MainContainer.getInstance().getClipboardManager().getText() != null) {
                    String clipboardData = MainContainer.getInstance().getClipboardManager().getText().toString();
                    Message message = Message.decode(clipboardData);
                    KeyPair keyPair = Cryptography.loadKeyPairFromMnemonic(MainContainer.getInstance().getMnemonic());
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