package com.Pl0;

import com.Entity.Token;
import com.Utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args)  throws IOException {
        String filePath = "D:\\Sole\\编译原理\\PL-0-JavaFX\\src\\com\\Source\\1.txt";
        PL0 pl0 = new PL0(FileUtil.readFile(new File(filePath)).strip()+'\n');
        ArrayList<Token> tokenList = pl0.lexicalAnalysis();
        for (Token token: tokenList) {
            System.out.println(token.toString());
        }
    }
}
