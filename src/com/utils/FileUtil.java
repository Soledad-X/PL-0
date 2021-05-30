package com.utils;

import java.io.*;

public class FileUtil {
    /**
     * 读取源程序文件的方法
     *
     * @param fileName 源程序文件路径
     * @return 返回读入的文件内容字符串
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuilder fileStringBuffer = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                fileStringBuffer.append(tempStr).append("\n");
            }
            reader.close();
            return fileStringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return fileStringBuffer.toString();
    }

    /**
     * 一次性写入字符串中所有内容
     * @param string 需要写入文件的字符串
     * @param file 对应写入路径的File对象
     */
    public static void saveFile(StringBuilder string,File file)throws IOException{
        Writer writer = new FileWriter(file);
        writer.write(string.toString());
        writer.close();
    }
}
