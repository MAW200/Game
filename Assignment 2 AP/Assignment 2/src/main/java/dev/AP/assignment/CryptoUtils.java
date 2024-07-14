package dev.AP.assignment;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

    public static SecretKey loadOrGenerateKey(String keyFilePath) throws IOException {
        try {
            if (Files.exists(Paths.get(keyFilePath))) {
                // Load the existing key
                byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilePath));
                return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
            } else {
                // Generate and save a new key
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(256); // for AES-256
                SecretKey secretKey = keyGen.generateKey();

                // Ensure the saves directory exists
                Files.createDirectories(Paths.get("saves"));

                try (FileOutputStream keyOut = new FileOutputStream(keyFilePath)) {
                    keyOut.write(secretKey.getEncoded());
                }

                System.out.println("Secret key generated and saved successfully.");
                return secretKey;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Failed to generate the secret key.", e);
        }
    }

    public static SecretKey loadKey(String keyFilePath) throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyFilePath));
        return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
    }

    public static byte[] encrypt(String data, SecretKey key) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes);
    }
}

