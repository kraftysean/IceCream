package com.tasty.icecream.temp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

public class BCCrypto {
    // By default on 128-bit encryption is supported => cryptKey can only be 16
    private static String cryptKey = "6543210987654321";

    public static void main(String[] args) throws Exception {
        String clearText = "www.java2s.com";

//        System.out.println(new String(input));
        Encrypt encrypt = new Encrypt(clearText).invoke();
        int ctLength = encrypt.getCtLength();
        byte[] cipherText = encrypt.getCipherText();
        System.out.println(cipherText.toString());
        System.out.println(decrypt(ctLength, cipherText));


    }

    private static String decrypt(int ctLength, byte[] cipherText) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] keyBytes = cryptKey.getBytes();

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        // decryption pass
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
        int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);
//        System.out.println(new String(plainText));
//        System.out.println(ptLength);
        return new String(plainText);
    }

    private static class Encrypt {
        private String input;
        private byte[] cipherText;
        private int ctLength;

        public Encrypt(String input) {
            this.input = input;
        }

        public byte[] getCipherText() {
            return cipherText;
        }

        public int getCtLength() {
            return ctLength;
        }

        public Encrypt invoke() throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
            byte[] inputBytes = input.getBytes();
            byte[] keyBytes = cryptKey.getBytes();

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

            // encryption pass
            cipher.init(Cipher.ENCRYPT_MODE, key);

            cipherText = new byte[cipher.getOutputSize(inputBytes.length)];
            ctLength = cipher.update(inputBytes, 0, inputBytes.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);
            System.out.println(new String(cipherText));
            System.out.println(ctLength);
            return this;
        }
    }
}