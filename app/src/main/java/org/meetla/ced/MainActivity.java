package org.meetla.ced;

import static org.meetla.ced.util.Cryptography.SECURITY_PROVIDER;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.meetla.ced.databinding.ActivityMainBinding;
import org.meetla.ced.model.Message;
import org.meetla.ced.util.Cryptography;
import org.meetla.ced.util.GZipUtils;
import org.meetla.ced.util.JsonUtils;
import org.nightcode.bip39.Bip39;
import org.nightcode.bip39.dictionary.Dictionary;
import org.nightcode.bip39.dictionary.EnglishDictionary;

import java.security.KeyPair;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {
    public static final String ECIESWITH_AES_CBC = "ECIESwithAES-CBC";

    private ActivityMainBinding binding;
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        EditText textEdit = findViewById(R.id.editTextTextMultiLine);

        SharedPreferences preferences = this.getSharedPreferences("security", Context.MODE_PRIVATE);
        String mnemonic;
        if(preferences.contains("mnemonic")){
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



                Dictionary dictionary = EnglishDictionary.instance();
                Bip39 bip39 = new Bip39(dictionary);
                byte[] seed = bip39.createSeed(mnemonic, "");
                KeyPair keyPair1 = Cryptography.generateKeyPair(seed);
                SecureRandom random = new SecureRandom();
                byte[] nonce = new byte[16];
                random.nextBytes(nonce);

                IESParameterSpec iesParamSpec = new IESParameterSpec(null, null, 256, 256, nonce, false);
                Cipher iesCipher = Cipher.getInstance(ECIESWITH_AES_CBC, SECURITY_PROVIDER);
                iesCipher.init(Cipher.ENCRYPT_MODE, keyPair1.getPublic(), iesParamSpec);

                byte[] ciphertext = iesCipher.doFinal(GZipUtils.gzip(textEdit.getText().toString().getBytes()));
                Message message = new Message();
                message.cipherData = ciphertext;
                message.nonce = nonce;

                clipboard.setText(message.encode());
                Toast.makeText(getApplicationContext(), "Encrypted", Toast.LENGTH_SHORT).show();
            } catch (Throwable t){
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


        Button buttonDec = findViewById(R.id.button);
        buttonDec.setOnClickListener(i -> {
            try {
                // NULLPOINTER
                String clipboardData = clipboard.getText().toString();
                Dictionary dictionary = EnglishDictionary.instance();
                Bip39 bip39 = new Bip39(dictionary);
                byte[] seed = bip39.createSeed(mnemonic, "");
                KeyPair keyPair1 = Cryptography.generateKeyPair(seed);

                Message message = Message.decode(clipboardData);

                IESParameterSpec iesParamSpec2 = new IESParameterSpec(null, null, 256, 256, message.nonce, false);
                Cipher decryptCipher = Cipher.getInstance(ECIESWITH_AES_CBC, SECURITY_PROVIDER);
                decryptCipher.init(Cipher.DECRYPT_MODE, keyPair1.getPrivate(), iesParamSpec2);
                byte[] plaintextBytes = decryptCipher.doFinal(message.cipherData);
                String decryptedText = new String(GZipUtils.gunzip(plaintextBytes));

                textEdit.setText(decryptedText);
                Toast.makeText(getApplicationContext(), "Decrypted", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

}