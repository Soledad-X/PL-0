package com.test;

import com.pl0.GrammarParser;
import com.utils.FileUtil;

public class TestPL0 {
    public static void main(String[] args) {
        String filePath = "D:\\Soledad-X\\编译原理\\PL-0-JavaFX\\src\\com\\Source\\1.txt";
        GrammarParser grammarParser = new GrammarParser(FileUtil.readFileContent(filePath));
        grammarParser.pl0();
    }
}
