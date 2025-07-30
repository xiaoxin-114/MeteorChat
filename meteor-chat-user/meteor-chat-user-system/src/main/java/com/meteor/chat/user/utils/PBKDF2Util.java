package com.meteor.chat.user.utils;

import cn.hutool.core.util.StrUtil;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.user.constants.UserConstants;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PBKDF2Util {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000; // 迭代次数
    private static final int KEY_LENGTH = 256;   // 密钥长度（位）
    private static final int SALT_LENGTH = 16;   // 盐值长度（字节）

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用 PBKDF2 + Salt 加密密码
     *
     * @param password 原始密码
     * @param salt     盐值（Base64 编码）
     * @return 加密后的哈希值（Base64 编码）
     */
    public static String hashPassword(String password, String salt) {
        if (StrUtil.isBlank(salt)) {
            throw new BusinessException("盐值不能为空");
        }
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_LENGTH);
            SecretKey key = skf.generateSecret(spec);
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 加密失败", e);
        }
    }

    /**
     * 验证用户输入的密码是否匹配存储的哈希值
     *
     * @param inputPass 用户输入的密码
     * @param storedHash 存储的哈希值
     * @param salt       存储的盐值
     * @return 是否匹配
     */
    public static boolean verifyPassword(String inputPass, String storedHash, String salt) {
        if (StrUtil.isBlank(salt)) {
            return false;
        }
        String hashedInput = hashPassword(inputPass, salt);
        return hashedInput.equals(storedHash);
    }

    public static String getDefaultPassword(String salt) {
        return hashPassword(UserConstants.DEFAULT_PASSWORD, salt);
    }
}

