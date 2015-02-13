package com.thoughtworks.zeratul.utils;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    private static final Logger log = Logger.getLogger(HashUtil.class);

    static public byte[] md5(String value) {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("MD5");
            return sha.digest(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.error("not support MD5 algorithm", e);
        }
        return null;
    }

    static public String md5Hex(String value) {
        return hexEncode(md5(value));
    }

    static public byte[] sha1(String value) {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
            return sha.digest(value.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.error("not support SHA-1 algorithm", e);
        }
        return null;
    }

    static public String sha1Hex(String value) {
        return hexEncode(sha1(value));
    }

    /**
     * The byte[] returned by MessageDigest does not have a nice
     * textual representation, so some form of encoding is usually performed.
     *
     * This implementation follows the example of David Flanagan's book
     * "Java In A Nutshell", and converts a byte array into a String
     * of hex characters.
     *
     * Another popular alternative is to use a "Base64" encoding.
     */
    static private String hexEncode(byte[] aInput){
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for (int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            result.append(digits[ (b&0xf0) >> 4 ]);
            result.append(digits[ b&0x0f]);
        }

        return result.toString();
    }
}
