package com.PL0;


import com.Entity.Token;
import com.Utils.StreamUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PL0 {
    private String source;
    public PL0(String source) throws IOException {
        this.source = source;
    }
    /**
     * 将字符串形式的source转化为输入流，使用lexicalAnalysis类对文件进行词法分析
     *
     * @return Token数组-词法分析的结果数组
     */
    public ArrayList<Token> lexicalAnalysis() throws IOException {
        ArrayList<Token> tokenArrayList;
        InputStream inputStream = StreamUtil.getStrToStream(source);
        // 构建FileInputStream对象
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        // 构建InputStreamReader对象,编码与写入相同
        lexicalAnalysis analysis = new lexicalAnalysis(reader);
        tokenArrayList = analysis.analysis();
        reader.close();
        // 关闭读取流
        inputStream.close();
        // 关闭输入流,释放系统资源
        return tokenArrayList;
    }
}
