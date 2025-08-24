package passwordmanager;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.UUID;

public class Encryption {
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 12;
    private static final int KEY_LEN = 256;
    private static final int ITERATIONS = 200000;
    private static final int TAG_LEN = 128;

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LEN);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void VerifyMasterPassword(char[] master, String token) throws Exception {
        decryptPassword(master, token);
    }

    public static String CreateVerificationToken(char[] master) throws Exception {
        String random32 = UUID.randomUUID().toString().replace("-", "");
        return encryptPassword(master, random32.getBytes());
    }

    public static String encryptPassword(char[] masterPassword, byte[] plainTextPassword)
            throws Exception {
        byte[] iv = new byte[IV_LEN];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        byte[] salt = new byte[SALT_LEN];
        random.nextBytes(salt);
        SecretKey key = deriveKey(masterPassword, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LEN, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] ciphertext = cipher.doFinal(plainTextPassword);

        ByteBuffer buffer = ByteBuffer.allocate(SALT_LEN + IV_LEN + ciphertext.length);
        buffer.put(salt);
        buffer.put(iv);
        buffer.put(ciphertext);

        return Base64.getEncoder().encodeToString(buffer.array());
    }

    public static byte[] decryptPassword(char[] masterPassword, String base64Data) throws Exception {
        byte[] data = Base64.getDecoder().decode(base64Data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte[] salt = new byte[SALT_LEN];
        buffer.get(salt);
        byte[] iv = new byte[IV_LEN];
        buffer.get(iv);
        byte[] ciphertext = new byte[buffer.remaining()];
        buffer.get(ciphertext);

        SecretKey key = deriveKey(masterPassword, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LEN, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(ciphertext);
    }
}
