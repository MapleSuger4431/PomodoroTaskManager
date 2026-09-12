package com.pomodoro.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希工具类，提供常用的摘要算法封装
 */
public class HashUtil {

    /** 私有构造方法，防止工具类被实例化 */
    private HashUtil() {
    }

    /**
     * 计算字符串的 SHA-256 摘要，返回小写十六进制字符串
     */
    public static String sha256(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前环境不支持 SHA-256 算法", e);
        }
    }
}
