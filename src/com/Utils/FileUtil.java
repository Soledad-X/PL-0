package com.Utils;

import java.io.*;

public class FileUtil {
    /**
     * 一次性读取文件的所有内容，以字符串形式输出
     * @param file 对应读取路径的File对象
     * @return 字符串
     */
    public static String readFile(File file) throws IOException{
        if (file != null && file.canRead()){
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            String content = new String(bytes,"UTF-8");
            fileInputStream.close();
            return content;
        }
        return "";
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
