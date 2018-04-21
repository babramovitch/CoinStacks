package com.nebulights.coinstacks.Network.exchanges.Quadriga;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by babramovitch on 2018-02-21.
 */

//TODO requires cleaning up


public class Hasher {

    public static String generateHashWithHmac256(byte[] message, byte[] key) {
        try {
            final String hashingAlgorithm = "HmacSHA256"; //or "HmacSHA1", "HmacSHA512"

            byte[] bytes = hmac(hashingAlgorithm, key, message);

           // final String messageDigest = bytesToHex(bytes);

            return Base64.encodeToString(bytes, Base64.NO_WRAP);

            //Log.i("TAG", "message digest: " + messageDigest);

            //return messageDigest;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String generateDigestHashWithHmac256(byte[] message, byte[] key) {
        try {
            final String hashingAlgorithm = "HmacSHA256"; //or "HmacSHA1", "HmacSHA512"

            byte[] bytes = hmac(hashingAlgorithm, key, message);

            final String messageDigest = bytesToHex(bytes);

            Log.i("TAG", "message digest: " + messageDigest);

            return messageDigest;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String generateGeminiDigestHashWithHmac256(byte[] message, byte[] key) {
        try {
            final String hashingAlgorithm = "HmacSHA384"; //or "HmacSHA1", "HmacSHA512"

            byte[] bytes = hmac(hashingAlgorithm, key, message);

            final String messageDigest = bytesToHex(bytes);

            Log.i("TAG", "message digest: " + messageDigest);

            return messageDigest;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
