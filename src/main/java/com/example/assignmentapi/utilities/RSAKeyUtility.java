package com.example.assignmentapi.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public final class RSAKeyUtility {
    static final String certsPath = "certificates/";
    static final String publicKeyName = "key.pub";
    static final String privateKeyName = "key.priv";

    static Scanner scan = new Scanner(System.in);

    // Running this generates new keys to the /certificates folder in root
    public static void main (String[] args) throws Exception {
        KeyPair keyPair = createKeys();
        writeKeyToFile(keyPair.getPublic(), certsPath + publicKeyName);
        System.out.println("\n----\n");
        writeKeyToFile(keyPair.getPrivate(), certsPath + privateKeyName);
    }

    // Generates RSA keypair
    public static KeyPair createKeys () throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    // Writes a key to file specified by fullPath
    public static void writeKeyToFile (Key key, String fullPath) {
        File file = new File(fullPath);

        // Ensure that the specified directory exists
        String path = file.getParent();
        if (path != null) {
            try {
                Files.createDirectory(Paths.get(path));
                System.out.println("Created directory " + path + "/");
            } catch (FileAlreadyExistsException ignore) {
                System.out.println("Directory already exists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Attempts to write the key to the specified path
        System.out.println("Writing key to " + fullPath);
        try {
            if (file.isFile()) {
                // File already exists at path, confirm overwriting file
                System.err.println(fullPath + " already exists! Overwrite? (Y/N)");
                String overWrite = scan.next();
                if (overWrite.equalsIgnoreCase("y")) {
                    try (FileOutputStream keyOutput = new FileOutputStream(fullPath)) { // try-with-resources
                        keyOutput.write(key.getEncoded());
                        System.out.println("Successfully wrote key to " + fullPath);
                    }
                } else {
                    System.out.println("Skipped writing key to " + fullPath);
                }
            } else {
                // File doesn't already exist, write to file
                try (FileOutputStream keyOutput = new FileOutputStream(fullPath)) {
                    keyOutput.write(key.getEncoded());
                    System.out.println("Successfully wrote public key to " + fullPath);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write key to file! " + e.getMessage());
        }
    }

    public static Key loadKeyFromFile (String fullPath) {
        File file = new File(fullPath);

        try {
            String fileName = file.getName();
            byte[] keyBytes = Files.readAllBytes(file.toPath());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            if (fileName.equals(publicKeyName)) {
                // Load public key (these require different algorithms)
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
                return keyFactory.generatePublic(publicKeySpec);
            } else if (fileName.equals(privateKeyName)) {
                // Load private key
                EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                return keyFactory.generatePrivate(keySpec);
            } else {
                throw new NoSuchFileException(fileName);
            }
        } catch (NoSuchFileException e) {
            System.err.println("No key found at " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException" + e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.err.println("InvalidKeySpecException" + e.getMessage());
        } catch (IOException ignored) {
        }
        return null;
    }
}
