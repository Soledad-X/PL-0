package com.test;

import com.PL0.GrammarParser;
import com.PL0.LexicalAnalyzer;
import com.entity.Token;
import com.Utils.FileUtil;
import org.junit.Test;
import java.util.ArrayList;

public class TestPL0 {
    @Test
    public void testLexicalAnalyzer() {
        String filePath = "D:\\Soledad-X\\编译原理\\PL-0-JavaFX\\src\\com\\Source\\1.txt";
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(FileUtil.readFileContent(filePath));
        ArrayList<Token> tokens = new ArrayList<>();
        Token token = lexicalAnalyzer.getSym();
        System.out.println(token);
        do{
            tokens.add(token);
            token = lexicalAnalyzer.getSym();
            System.out.println(token);
        }while(!token.getSYM().equals("period"));

    }

    public static void main(String[] args) {
        String filePath = "D:\\Soledad-X\\编译原理\\PL-0-JavaFX\\src\\com\\Source\\1.txt";
        GrammarParser grammarParser = new GrammarParser(filePath);
        grammarParser.pl0();
    }
}
