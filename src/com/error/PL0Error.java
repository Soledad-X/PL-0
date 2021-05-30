package com.error;

import java.util.HashMap;
import java.util.Map;

public class PL0Error {
    private static final Map<Integer, String> error;
    static {
        error = new HashMap<>();
        error.put(1,"常数说明中的=写成:=");
        error.put(2,"常数说明中的=后应是数字");
        error.put(3,"常数说明中的标识符后应是=");
        error.put(4,"应是标识符");
        error.put(5,"漏掉了.或;");
        error.put(6,"过程说明后的符号不正确（应是语句开始符，或过程定义符）");
        error.put(7,"应是语句开始符");
        error.put(8,"程序体内语句部分的后跟符不正确");
        error.put(9,"程序结尾丢了句号.");
        error.put(10,"语句之间漏了;");
        error.put(11,"标识符未说明");
        error.put(12,"赋值语句中，赋值号左部标识符属性应是变量");
        error.put(13,"赋值语句左部标识符后应是赋值号:=");
        error.put(14,"call后应为标识符");
        error.put(15,"call后标识符属性应为过程");
        error.put(16,"条件语句中丢了then");
        error.put(17,"丢了end或;");
        error.put(18,"while型循环语句中丢了do");
        error.put(19,"语句后的符号不正确");
        error.put(20,"应为关系运算符");
        error.put(21,"表达式内标识符属性不能是过程");
        error.put(22,"表达式中漏掉右括号)");
        error.put(23,"因子后的非法符号");
        error.put(24,"表达式的开始符不能是此符号");
        error.put(31,"数越界");
        error.put(32,"过程嵌套层数大于最大允许的套层数");
        error.put(33,"read、write语句的后跟符应为)");
        error.put(34,"read、write保留字后应接(");
        error.put(35,"read语句中标识符应为变量");
        error.put(36,"read、write语句括号中应接标识符");

    }
    public static String log(int indexError) {
        return error.get(indexError);
    }
}
