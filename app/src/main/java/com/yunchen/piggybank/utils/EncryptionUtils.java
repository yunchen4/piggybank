package com.yunchen.piggybank.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class EncryptionUtils {

    public static String getEncryptedPwd(String pwd){
        try {
            pwd = pwd+pwd.substring(2,5);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(pwd.getBytes(StandardCharsets.UTF_8));
            byte[] encryption = md5.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : encryption) {
                if (Integer.toHexString(0xff & b).length() == 1) {
                    stringBuilder.append("0").append(Integer.toHexString(0xff & b));
                } else {
                    stringBuilder.append(Integer.toHexString(0xff & b));
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
