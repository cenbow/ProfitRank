package com.mr.util;

/**
 * Created by Administrator on 2017/8/9.
 */

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;


public class Base64Util {

    /**
     * base64加密方法
     * @param  text
     * @return encoded
     */
    public static String getEncodedBase64(String text,String charset){
        final Base64 base64 = new Base64();
        final byte[] textByte;
        String encodedText=null;
        try {
            textByte = text.getBytes(charset);
            encodedText = base64.encodeToString(textByte);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedText;
    }

    /**
     * base64解密方法
     *
     * @param text
     * @return
     */
    public static String getDecodedBase64(String text,String charset){
        final Base64 base64 = new Base64();
        String decodeText=null;
        try {
            final byte[] textByte = text.getBytes(charset);
            decodeText=new String(base64.decode(textByte), charset);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decodeText;
    }
}