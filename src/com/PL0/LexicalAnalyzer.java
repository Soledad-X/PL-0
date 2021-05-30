package com.PL0;

import com.entity.Token;

import java.util.HashMap;
import java.util.Map;

public class LexicalAnalyzer {
    private final String[] sourceByRow; //行式源程序;
    private final Map<String, String> WORD; //保留字表
    private final int numMax; //数字允许的最长位数
    private final int AMax; //标识符最长长度
    private final int Row; //源程序行数
    private final int Column; //源程序一行允许的最多字符数
    private char CH; //存放当前读取的字符，初值为空('\0')
    private int LL; //计数器，初值为-1，记录当前读取的行式源程序下标
    private int CC; //计数器，初值为-1，记录当前读取的字符下标
    private String Line; //缓冲区：最多容纳Column个字符

    public LexicalAnalyzer(String source) {
        //常量初始化
        sourceByRow = source.split("\n");
        WORD = new HashMap<>();
        numMax = 10;
        AMax = 10;
        Row = sourceByRow.length;
        Column = 80;

        //变量初始化
        CH = '\0';
        LL = -1;
        CC = -1;
        Line = "";

        WORD.put("begin", "beginSym");
        WORD.put("end", "endSym");
        WORD.put("const", "constSym");
        WORD.put("var", "varSym");
        WORD.put("do", "doSym");
        WORD.put("while", "whileSym");
        WORD.put("if", "ifSym");
        WORD.put("then", "thenSym");
        WORD.put("read", "readSym");
        WORD.put("write", "writeSym");
        WORD.put("procedure", "procedureSym");
        WORD.put("call", "callSym");
        WORD.put("odd", "oddSym");

        WORD.put("+", "plus");
        WORD.put("-", "minus");
        WORD.put("*", "times");
        WORD.put("/", "slash");
        WORD.put("=", "eql");
        WORD.put("#", "neq");
        WORD.put("<", "lss");
        WORD.put("<=", "leq");
        WORD.put(">", "gtr");
        WORD.put(">=", "geq");
        WORD.put(":=", "becomes");
        WORD.put("(", "lparen");
        WORD.put(")", "rparen");
        WORD.put(",", "comma");
        WORD.put(";", "semicolon");
        WORD.put(".", "period");
    }

    /**
     * 获取单词
     *
     * @return Token对象-代表一个词
     */
    public Token getSym() {
        ignoreBlankSpace();
        if (Character.isLetter(CH)) { //首字符为字母
            int K = 0;
            StringBuilder A = new StringBuilder(); //识别标识符和保留字的缓存
            do {
                if (K < AMax) {
                    K++;
                    A.append(CH);
                }
                getCh();
            } while (Character.isLetterOrDigit(CH));
            //获取A的最终结果
            String ID = A.toString();
            if (WORD.containsKey(ID)) { //ID为保留字
                // 相应保留字类型送SYM
                return new Token(WORD.get(ID));
            } else { //ID为标识符
                return new Token("identifier", ID);
            }
        } else { //首字符不为字母
            if (Character.isDigit(CH)) { //首字符为数字
                int K = 0;
                int NUM = 0;
                do{
                    if(K < numMax){
                        K++;
                        NUM = NUM * 10 + Character.getNumericValue(CH);
                    }
                    getCh();
                }while (Character.isDigit(CH));
                return new Token("number", NUM);
            } else { //首字符不为数字
                if (CH == ':' || CH == '<' || CH == '>') {
                    StringBuilder A = new StringBuilder();
                    A.append(CH);
                    getCh();
                    if (CH == '=') {
                        A.append(CH);
                        getCh();
                        return new Token(WORD.get(A.toString()));
                    } else if (A.toString().equals(":")) {
                        // 打印出错信息
                        System.out.printf("Line %d:不能单独使用:(引号)\n",LL+1);
                        // 停止编译
                        System.exit(0);
                    } else {
                        return new Token(WORD.get(A.toString()));
                    }
                } else if (WORD.containsKey(String.valueOf(CH))) { //首字符为单字运算符/单字界符
                    Token token = new Token(WORD.get(String.valueOf(CH)));
                    if(CH != '.') getCh(); //避免out of range
                    return token;
                } else {
                    // 打印出错信息
                    System.out.printf("Line %d:出现非法字符\n",LL+1);
                    // 停止编译
                    System.exit(0);
                }
            }
        }
        return null;
    }

    /**
     * 过滤' '和'\t'
     */
    private void ignoreBlankSpace() {
        while (CH == ' ' || CH == '\t' || CH == '\0') {
            getCh();
        }
    }

    /**
     * 获取下一个字符CH
     */
    private void getCh() { //判断缓冲区是否有字符
        if (CC == -1 || (CC + 1) >= Line.length()) { //缓冲区无字符，判断源程序是否结束
            if ((LL + 1) < Row) { //源程序未结束
                if (sourceByRow[LL + 1].length() <= Column) { // 源程序一行的字符数未超过允许的最多字符数Column
                    LL++;
                    CC = -1;
                    Line = sourceByRow[LL];
                    System.out.println(Line);
                } else {
                    // 打印出错信息
                    System.out.printf("Line %d:源程序中一行最多允许%d个字符\n%n", LL+1,Column);
                    // 停止编译
                    System.exit(0);
                }
            } else { //源程序已结束
                //打印出错信息
                System.out.println("源程序不完整");
                //停止编译
                System.exit(0);
            }
        }
        CC++;
        CH = Line.charAt(CC);
    }
}
