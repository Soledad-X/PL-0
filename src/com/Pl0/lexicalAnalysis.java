package com.Pl0;

import com.Entity.Token;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class lexicalAnalysis {
    private final InputStreamReader reader;
    private final ArrayList<Token> tokenArrayList;
    private final Map<String, String> stayWords;
    private int row;    //进行第row行源程序的分析
    private char ch;    //当前分析的字符

    public lexicalAnalysis(InputStreamReader reader) throws IOException {
        this.reader = reader;
        this.tokenArrayList = new ArrayList<>();
        this.stayWords = new HashMap<>();
        this.row = 1;
        this.ch = getCh();
        stayWords.put("begin", "beginSym");
        stayWords.put("end", "endSym");
        stayWords.put("const", "constSym");
        stayWords.put("var", "varSym");
        stayWords.put("do", "doSym");
        stayWords.put("while", "whileSym");
        stayWords.put("if", "ifSym");
        stayWords.put("then", "thenSym");
        stayWords.put("read", "readSym");
        stayWords.put("write", "writeSym");
        stayWords.put("procedure", "procedureSym");
        stayWords.put("call", "callSym");
        stayWords.put("odd", "oddSym");

        stayWords.put("+", "plus");
        stayWords.put("-", "minus");
        stayWords.put("*", "times");
        stayWords.put("/", "slash");
        stayWords.put("=", "eql");
        stayWords.put("#", "neq");
        stayWords.put("<", "lss");
        stayWords.put("<=", "leq");
        stayWords.put(">", "gtr");
        stayWords.put(">=", "geq");
        stayWords.put(":=", "becomes");
        stayWords.put("(", "lparen");
        stayWords.put(")", "rparen");
        stayWords.put(",", "comma");
        stayWords.put(";", "semicolon");
        stayWords.put(".", "period");
    }

    /**
     * 对reader对应的文件进行词法分析
     *
     * @return Token数组-词法分析的结果数组
     */
    public ArrayList<Token> analysis() throws IOException {
        do {
            tokenArrayList.add(getSym());
        } while (reader.ready());
        return tokenArrayList;
    }

    /**
     * 获取单词
     *
     * @return Token对象-代表一个词
     */
    private Token getSym() throws IOException {
        Token token;
        while (ch == ' ' || ch == '\t' || ch == '\n') {
            if (ch == '\n'){
                row++;
            }
            ch = getCh();
        }
        StringBuilder word = new StringBuilder();
        if (Character.isLetter(ch)) {
            //识别关键字和标识符
            word.append(ch);
            do {
                ch = getCh();
                if (Character.isLetter(ch) || Character.isDigit(ch)) {
                    word.append(ch);
                } else break;
            } while (true);
            token = new Token(row, stayWords.getOrDefault(word.toString(), "identifier"), word.toString());
        } else if (Character.isDigit(ch)) {
            //识别常数
            word.append(ch);
            do {
                ch = getCh();
                if (Character.isDigit(ch)) {
                    word.append(ch);
                } else break;
            } while (true);
            token = new Token(row, "number", word.toString());
        } else if (ch == '<' || ch == '>' || ch == ':') {
            //识别<,<=,:=,>=,>
            word.append(ch);
            ch = getCh();
            if (ch == '=') {
                word.append(ch);
                ch = getCh();
            }
            token = new Token(row, stayWords.getOrDefault(word.toString(), "error:"), word.toString());
        } else if (stayWords.containsKey(String.valueOf(ch))) {
            //识别其余单字运算符或界符
            token = new Token(row, stayWords.get(String.valueOf(ch)), String.valueOf(ch));
            ch = getCh();
        } else {
            //非法字符
            token = new Token(row, "error", String.valueOf(ch));
            ch = getCh();
        }
        return token;
    }

    /**
     * 使用reader.read()读取一个字符(int)，转化为(char)
     *
     * @return char类型的字符
     */
    private char getCh() throws IOException {
        return (char) reader.read();
    }
}
