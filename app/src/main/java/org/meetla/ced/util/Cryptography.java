package org.meetla.ced.util;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Cryptography {
    public static final Provider SECURITY_PROVIDER = new BouncyCastleProvider();

    public static final String SHA_1_WITH_ECDSA = "SHA1withECDSA";
    public static final String EC = "EC";
    public static final String SHA_1_PRNG = "SHA1PRNG";
    public static final String ECIE_SWITH_AES = "ECIESwithAES-CBC";


    public static KeyPair generateKeyPair() {
        return generateKeyPair(null);
    }

    public static KeyPair generateKeyPair(byte[] seed) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(EC, SECURITY_PROVIDER);
            CustomSecureRandom random = new CustomSecureRandom(seed);

            ECGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
            keyGen.initialize(spec, new java.security.SecureRandom() {
                @Override
                public void nextBytes(byte[] bytes) {
                    try {
                        byte[] randomBytes = random.nextBytes(bytes.length);
                        System.arraycopy(randomBytes, 0, bytes, 0, bytes.length);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            KeyPair keyPair = keyGen.generateKeyPair();
            return keyPair;
        } catch (Throwable t) {
            return null;
        }
    }

    public static PublicKey loadPublicKey(byte[] encodedKey) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedKey);
            KeyFactory kf = KeyFactory.getInstance(EC);
            return kf.generatePublic(spec);
        } catch (Throwable t) {
            return null;
        }
    }

    public static PrivateKey loadPrivateKey(byte[] encodedKey) {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory kf = KeyFactory.getInstance(EC);
            return kf.generatePrivate(spec);
        } catch (Throwable t) {
            return null;
        }
    }

    public static byte[] sign(byte[] data, PrivateKey key) {
        try {
            Signature dsa = Signature.getInstance(SHA_1_WITH_ECDSA);
            dsa.initSign(key);
            dsa.update(data);
            return dsa.sign();
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            return null;
        }
    }

    public static Boolean verify(PublicKey publicKey, byte[] signed, byte[] data) {
        try {
            Signature signature = Signature.getInstance(SHA_1_WITH_ECDSA);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signed);
        } catch (Throwable t) {
            return null;
        }
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher iesCipher = Cipher.getInstance(ECIE_SWITH_AES);
            iesCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return iesCipher.doFinal(data);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
        return null;
    }

}
