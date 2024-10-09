package org.meetla.ced.ui.home;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.meetla.ced.R;
import org.meetla.ced.databinding.FragmentHomeBinding;
import org.meetla.ced.model.Message;
import org.meetla.ced.util.Cryptography;
import org.meetla.ced.util.MainContainer;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ClipboardManager clipboardManager = (ClipboardManager) binding.getRoot().getContext().getSystemService(CLIPBOARD_SERVICE);

        EditText textEdit = binding.editTextTextMultiLine;

        Button buttonClear = binding.getRoot().findViewById(R.id.clearButton);
        buttonClear.setOnClickListener(i -> {
            textEdit.setText("");
        });

        Button buttonEnc = binding.getRoot().findViewById(R.id.encryptButton);
        buttonEnc.setOnClickListener(i -> {
            try {
                //String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair1.getPublic().getEncoded());
                String publicKeyBase64 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE4tODL6kkdfcB4ZbpaeC3h0e4+C/LqboPM6W9M2LpKg+Va8NCfMoBPBbvd0LMdfW2BbiR7TkG7ZntZ1pfmMExlw==";
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
                PublicKey publicKey = Cryptography.loadPublicKey(publicKeyBytes);
                Message message = Cryptography.encrypt(textEdit.getText().toString().getBytes(), publicKey);
                clipboardManager.setText(message.encode());
                Toast.makeText(binding.getRoot().getContext(), "Encrypted and Copied!", Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonDec = binding.getRoot().findViewById(R.id.decryptButton);
        buttonDec.setOnClickListener(i -> {
            try {
                if (clipboardManager.getText() != null) {
                    String clipboardData = clipboardManager.getText().toString();
                    Message message = Message.decode(clipboardData);
                    KeyPair keyPair = Cryptography.loadKeyPairFromMnemonic(MainContainer.getInstance().getMnemonic(binding.getRoot().getContext()));
                    textEdit.setText(new String(Cryptography.decrypt(message, keyPair.getPrivate())));
                    Toast.makeText(binding.getRoot().getContext(), "Decrypted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(binding.getRoot().getContext(), "Empty clipboard", Toast.LENGTH_SHORT).show();
                }
            } catch (Throwable t) {
                t.printStackTrace();
                Toast.makeText(binding.getRoot().getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}