package com.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 字符串和输入流互转类
 */
public class StreamUtil {
    /**
     * 将一个字符串转化为输入流
     *
     * @param inputString 字符串
     * @return 输入流
     */
    public static InputStream getStrToStream(String inputString) {
        if (inputString != null && !inputString.trim().equals("")) {
            try {
                return new ByteArrayInputStream(inputString.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将一个输入流转化为字符串
     *
     * @param inputStream 输入流
     * @return 字符串
     */
    public static String getStreamToStr(InputStream inputStream) {
        if (inputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String tempOneLine;
                while ((tempOneLine = tBufferedReader.readLine()) != null) {
                    stringBuilder.append(tempOneLine);
                }
                return stringBuilder.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
