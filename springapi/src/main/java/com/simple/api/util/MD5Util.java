package com.simple.api.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类，包括的功能有
 * 1、计算文件内容的MD5值
 *
 */
public class MD5Util {

    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return getMD5(fileInputStream);
    }

    public static String getMD5(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest MD5 = MessageDigest.getInstance("MD5"); //NOSONAR
        byte[] buffer = new byte[8192];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            MD5.update(buffer, 0, length);
        }
        return new String(Hex.encodeHex(MD5.digest()));
    }

    /**
     * 求一个字符串的md5值
     *
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target); //NOSONAR
    }

}
