package com.example.assignmentapi.utilities;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;

@Component
public final class RSAKeyUtility {

    public static Key loadKeyFromFile (String fullPath) {
        File file = new File(fullPath);
        try {
            String fileName = file.getName();
            byte[] keyBytes = Files.readAllBytes(file.toPath());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            String publicKeyName = "key.pub";
            String privateKeyName = "key.priv";
            if (fileName.equals(publicKeyName)) {
                // Load public key (these require different algorithms)
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
                return keyFactory.generatePublic(publicKeySpec);
            } else if (fileName.equals(privateKeyName)) {
                // Load private key
                EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                return keyFactory.generatePrivate(keySpec);
            } else {
                throw new NoSuchFileException(fullPath);
            }
        } catch (NoSuchFileException e) {
            System.err.println("No matching key found at " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException" + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.err.println("InvalidKeySpecException" + e.getMessage());
        } catch (IOException ignored) {
        }
        return null;
    }
}
