package com.pl0;

import com.entity.Code;
import com.entity.Declaration;
import com.entity.Token;
import com.error.Error;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class GrammarParser {
    //常量声明
    private final String[] identBegSys;
    private final String[] commaBegSys;
    private final String[] endSemiBegSys;
    private final String[] doThenBegSys;
    private final String[] exprBegSys;
    private final String[] termBegSys;
    private final String[] condBegSys;
    private final String[] declBegSys;
    private final String[] statBegSys;
    private final String[] facBegSys;
    private final Integer CXMax; //目标代码最大长度（可容纳代码行数）
    private final Integer NumMAx; //number的最大值
    private final Integer levelMax; //最大允许的块嵌套层数
    private final int[] S; //S为栈式计算机的数据内存区

    //变量声明
    private final LexicalAnalyzer lexicalAnalyzer; //词法分析器，提供getSym()方法
    private final ArrayList<Token> tokens; //单词表
    private final ArrayList<Declaration> declarations; //声明表，即为Table
    private final ArrayList<String> errors; //语法错误列表
    private final ArrayList<Code> codes; //目标代码列表
    private Token token;
    private String SYM;
    private String ID;
    private int NUM;

    public GrammarParser(String source) {
        //常量初始化
        int stackSize = 500; //假想的栈式计算机由500个栈单元
        CXMax = 200;
        NumMAx = 1000000;
        levelMax = 3;
        identBegSys = new String[]{"identifier", "procedureSym"};
        commaBegSys = new String[]{"rparen", "comma"};
        endSemiBegSys = new String[]{"semicolon", "endSym"};
        doThenBegSys = new String[]{"thenSym", "doSym"};
        exprBegSys = new String[]{"plus", "minus"};
        termBegSys = new String[]{"times", "slash"};
        condBegSys = new String[]{"eql", "neq", "lss", "leq", "gtr", "geq"};
        declBegSys = new String[]{"constSym", "varSym", "procedureSym"};
        statBegSys = new String[]{"beginSym", "callSym", "ifSym", "whileSym", "readSym", "writeSym", "identifier"};
        facBegSys = new String[]{"identifier", "number", "lparen"};
        S = new int[stackSize];
        //变量初始化
        lexicalAnalyzer = new LexicalAnalyzer(source);
        tokens = new ArrayList<>();
        declarations = new ArrayList<>();
        declarations.add(new Declaration()); //占用零号指针位，用于position时倒退
        errors = new ArrayList<>();
        codes = new ArrayList<>();

    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public ArrayList<Declaration> getDeclarations() {
        return declarations;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public ArrayList<Code> getCodes() {
        return codes;
    }

    /**
     * 主程序
     * <程序> ::= <分程序>.
     */
    public void pl0() {
        getToken();
        block(0, ArrayUtils.addAll(declBegSys, ArrayUtils.addAll(statBegSys, "period".split("@"))));
        if (!SYM.equals("period")) errors.add(token.getROW() + ": " + Error.log(9)); //主程序分析结束，应遇到表明程序结束的句号
        Declaration declaration0 = declarations.get(0);
        declaration0.setName("main");
        declaration0.setKind("procedureSym");
        declaration0.setLevel(-1);
    }

    /**
     * 语法分析过程处理过程
     *
     * @param level 语法分析所在的层次
     * @param FSys  用于出错恢复的单词集合
     */
    private void block(Integer level, String[] FSys) {
        Integer DX; //数据段内存分配指针，指向下一个被分配空间在数据段中的偏移位置
        int TX0; //记录本层开始时符号表位置
        int CX0; //记录本层开始时代码段分配位置

        DX = 3; //地址指示器给出每层局部量当前已分配到的相对位置。置初始值为3的原因是：每一层最开始的位置有三个空间用于存放静态链SL、动态链DL和返回地址RA
        TX0 = declarations.size();//初始符号表指针指向当前层的符号在符号表中的开始位置
        Declaration declaration = declarations.get(declarations.size() - 1);
        declaration.setAddress(codes.size());
        gen("JMP", 0, 0); //产生一行跳转指令，跳转位置暂时未知填0
        if (level > levelMax) { //如果当前过程嵌套层数大于最大允许的套层数
            errors.add(token.getROW() + ": " + Error.log(32));
        }
        do {
            if (SYM.equals("constSym")) { //token是const保留字，开始进行常量声明
                getToken(); //获取下一个token,正常因为用作常量名的表示符
                if (SYM.equals("identifier")) {
                    DX = constDeclaration(level, DX);
                    while (SYM.equals("comma")) {
                        getToken();
                        DX = constDeclaration(level, DX);
                    }
                    if (SYM.equals("semicolon")) { //如果常量声明结束，应遇到分号
                        getToken();
                    } else {
                        errors.add(token.getROW() + ": " + Error.log(5)); //漏掉了.或;
                    }
                }
            }
            if (SYM.equals("varSym")) { //token是var保留字，开始进行变量声明
                getToken(); //获取下一个token,正常因为用作变量名的表示符
                if (SYM.equals("identifier")) {
                    DX = varDeclaration(level, DX);
                    while (SYM.equals("comma")) {
                        getToken();
                        DX = varDeclaration(level, DX);
                    }
                    if (SYM.equals("semicolon")) { //如果常量声明结束，应遇到分号
                        getToken();
                    } else {
                        errors.add(token.getROW() + ": " + Error.log(5)); //漏掉了.或;
                    }
                }
            }
            while (SYM.equals("procedureSym")) {
                getToken(); //获取下一个token,正常因为用作过程名的表示符
                if (SYM.equals("identifier")) {
                    enter(level, DX, "procedureSym"); //将该过程登录到声明表中
                    getToken(); //获取下一个token，正常情况应为分号
                } else {
                    errors.add(token.getROW() + ": " + Error.log(4)); //应是标识符
                }
                if (SYM.equals("semicolon")) { //如果常量声明结束，应遇到分号
                    getToken();
                } else {
                    errors.add(token.getROW() + ": " + Error.log(5)); //漏掉了.或;
                }
                //递归调用语法分析过程，当前层次加一，同时传递合法单词符
                block(level + 1, ArrayUtils.addAll(FSys, "semicolon".split("@")));
                if (SYM.equals("semicolon")) { //递归返回后当前token应为递归调用时的最后一个end后的分号
                    getToken();
                    test(ArrayUtils.addAll(statBegSys, identBegSys), FSys, 6);
                } else {
                    errors.add(token.getROW() + ": " + Error.log(5)); //漏掉了.或;
                }
            }
        } while (Arrays.asList(declBegSys).contains(SYM));
        test(statBegSys, declBegSys, 7);
        //将前面生成的跳转语句的跳转位置改成当前位置
        codes.get(declarations.get(TX0 - 1).getAddress()).setAddressOffset(codes.size());
        Declaration declaration1 = declarations.get(TX0 - 1);
        declaration1.setAddress(codes.size()); //地址为当前代码分配地址
        declaration1.setSize(DX); //长度为当前数据段分配位置
        CX0 = codes.size(); //记下当前代码分配位置
        gen("INT", 0, DX); //生成分配空间指令，分配DX个空间
        statement(level, ArrayUtils.addAll(FSys, endSemiBegSys));
        gen("OPR", 0, 0); //生成从子程序返回操作指令
        test(FSys, null, 8); //用FSys检查当前状态是否合法，不合法则抛8号错
        listCode(CX0);
    }

    /**
     * 语句部分处理
     * <语句> ::= <赋值语句> | <条件语句> | <当型循环语句> | <过程调用语句> | <读语句> | <写语句> | <复合语句> | <空>
     * <赋值语句> ::= <标识符>:=<表达式>
     * <条件语句> ::= IF<条件>THEN<语句>
     * <当型循环语句> ::= WHILE<条件>DO<语句>
     * <过程调用语句> ::= CALL<标识符>
     * <读语句> ::= READ‘(’<标识符>{ ,<标识符> }‘)’
     * <写语句> ::= WRITE‘(’<表达式>{ ,<表达式> }‘)’
     * <复合语句> ::= BEGIN<语句>{;<语句>}END
     * <标识符> ::= <字母>{ <字母>|<数字> }
     *
     * @param level 语句分析所在的层次
     * @param FSys  用于出错恢复的单词集合
     */
    private void statement(Integer level, String[] FSys) {
        int CX1, CX2, i;
        Declaration declaration = null;
        //如果分析完一句后遇到分号或语句开始符循环分析下一句语句
        switch (SYM) {
            case "identifier" -> { //赋值语句
                i = position(ID); //在声明表中查到该标识符所在位置
                if (i == 0) errors.add(token.getROW() + ": " + Error.log(11)); //标识符未说明
                else {
                    declaration = declarations.get(i);
                    if (!declaration.getKind().equals("varSym")) { //如果在声明表中找到该标识符，但该标识符不是变量名
                        errors.add(token.getROW() + ": " + Error.log(12)); //赋值语句中，赋值号左部标识符属性应是变量
                        i = 0; //i置0作为错误标志
                    }
                }
                getToken(); //获得下一个token，正常应为赋值号
                if (SYM.equals("becomes")) getToken();
                else errors.add(token.getROW() + ": " + Error.log(13)); //赋值语句左部标识符后应是赋值号:=
                expression(level, FSys);
                if (i != 0) { //如果不曾出错，i将不为0，i所指为当前语名左部标识符在符号表中的位置
                    gen("STO", level - declaration.getLevel(), declaration.getAddress()); //产生一行把表达式值写往指定内存的STO目标代码
                }
            }
            case "readSym" -> { //read语句
                getToken(); //获得下一token，正常情况下应为左括号
                if (!SYM.equals("lparen")) {
                    errors.add(token.getROW() + ": " + Error.log(34)); //read保留字后应接(
                } else {
                    do {
                        getToken(); //获得下一个token，正常应是一个变量名
                        if (SYM.equals("identifier")) {
                            i = position(ID); //查符号表，找到它所在位置给i，找不到时i会为0
                            declaration = declarations.get(i);
                            if (i != 0 && !declaration.getKind().equals("varSym")) i = -1;
                        } else i = 0; //不是标识符则有问题，i置0作为出错标志

                        if (i == -1) errors.add(token.getROW() + ": " + Error.log(35)); //read语句中标识符应为变量
                        else if (i == 0) errors.add(token.getROW() + ": " + Error.log(36)); //read语句括号中应接变量标识符
                        else {
                            gen("OPR", 0, 16); //生成16号操作指令:从键盘读入数字
                            //生成sto指令，把读入的值存入指定变量所在的空间
                            gen("STO", level - declaration.getLevel(), declaration.getAddress());
                        }
                        getToken();
                    } while (SYM.equals("comma"));
                    if (!SYM.equals("rparen")) {
                        errors.add(token.getROW() + ": " + Error.log(33)); //read语句的后跟符应为)
                        while (!Arrays.asList(FSys).contains(SYM)) getToken(); //依靠FSys集，找到下一个合法的token，恢复语法分析
                    } else getToken(); //如果read语句正常结束，得到下一个token，一般为分号或end
                }
            }
            case "writeSym" -> { //write语句
                getToken(); //获得下一token，正常情况下应为左括号
                if (!SYM.equals("lparen")) {
                    errors.add(token.getROW() + ": " + Error.log(34)); //write保留字后应接(
                } else {
                    do {
                        getToken(); //获得下一个token，正常应是一个标识符
                        expression(level, ArrayUtils.addAll(FSys, commaBegSys));
                        gen("OPR", 0, 14); //生成14号指令：向屏幕输出
                    } while (SYM.equals("comma"));
                    if (!SYM.equals("rparen")) {
                        errors.add(token.getROW() + ": " + Error.log(33)); //read语句的后跟符应为)
                        while (!Arrays.asList(FSys).contains(SYM)) getToken(); //依靠FSys集，找到下一个合法的token，恢复语法分析
                    } else getToken(); //如果read语句正常结束，得到下一个token，一般为分号或end
                }
                gen("OPR", 0, 15);
            }
            case "callSym" -> { //call语句
                getToken(); //获得下一token，正常情况下应为过程名标识符
                if (SYM.equals("identifier")) {
                    i = position(ID); //查符号表，找到它所在位置给i，找不到时i会为0
                    declaration = declarations.get(i);
                    if (i == 0) errors.add(token.getROW() + ": " + Error.log(11)); //标识符未说明
                    else {
                        if (declaration.getKind().equals("procedureSym"))
                            gen("CAL", level - declaration.getLevel(), declaration.getAddress());
                        else errors.add(token.getROW() + ": " + Error.log(15)); //call后标识符属性应为过程
                    }
                    getToken();
                } else errors.add(token.getROW() + ": " + Error.log(14)); //call后应为标识符
            }
            case "ifSym" -> { //if语句
                getToken(); //获得下一token，正常情况下应为逻辑表达式

                condition(level, ArrayUtils.addAll(FSys, doThenBegSys)); //对逻辑表达式进行分析计算，出错恢复集中加入then和do语句

                if (SYM.equals("thenSym")) { //表达式后应遇到then语句
                    getToken(); ////获得下一token，正常情况下应为语句
                } else errors.add(token.getROW() + ": " + Error.log(16)); //条件语句中丢了then
                CX1 = codes.size(); //记下当前代码分配指针位置

                gen("JPC", 0, 0); //生成条件跳转指令，跳转位置暂时填0，分析完语句后再填写

                statement(level, FSys); //分析then后的语句

                Code code = codes.get(CX1);
                code.setAddressOffset(codes.size()); //上一行指令(cx1所指的)的跳转位置应为当前CX所指位置
            }
            case "beginSym" -> { //begin语句
                getToken(); //获得下一token，正常情况下应为逻辑表达式
                statement(level, FSys); //分析begin后的语句
                while (Arrays.asList(ArrayUtils.addAll(statBegSys, "semicolon".split("@"))).contains(SYM)) {
                    if (SYM.equals("semicolon")) getToken(); //如果语句是分号（可能是空语句）
                    else errors.add(token.getROW() + ": " + Error.log(10)); //语句之间漏了;
                    statement(level, FSys);
                }
                if (SYM.equals("endSym")) { //如果语句全分析完了，应该遇到end
                    getToken();
                } else {
                    errors.add(token.getROW() + ": " + Error.log(17)); //丢了end或;
                }
            }
            case "whileSym" -> { //while语句
                CX1 = codes.size(); //记下当前代码指针分配位置，这是while循环的开始位置

                getToken(); //获得下一token，正常情况下应为逻辑表达式

                condition(level, ArrayUtils.addAll(FSys, doThenBegSys)); //对逻辑表达式进行分析计算，出错恢复集中加入then和do语句

                CX2 = codes.size(); //记下当前代码分配位置，这是while的do中的语句的开始位置

                gen("JPC", 0, 0); //生成条件跳转指令，跳转位置暂时填0，分析完语句后再填写

                if (SYM.equals("doSym")) { //表达式后应为do语句
                    getToken(); ////获得下一token，正常情况下应为语句
                } else errors.add(token.getROW() + ": " + Error.log(18)); //while型循环语句中丢了do
                statement(level, FSys); //分析do后的语句

                gen("JMP", 0, CX1); //循环跳转到CX1位置，即再次进行逻辑判断

                Code code = codes.get(CX2);
                code.setAddressOffset(codes.size()); //上一行指令(CX2所指的)的跳转位置应为当前CX所指位置
            }
        }
        test(FSys, null, 19); //至此一个语句处理完成，一定会遇到FSys集中的符号，如果没有遇到，就抛19号错
    }

    /**
     * 表达式处理
     * <表达式> ::= [+ | -]<项>{<加法运算符><项>}
     * <加法运算符> ::= + | -
     *
     * @param level 表达式分析所在的层次
     * @param FSys  用于出错恢复的单词集合
     */
    private void expression(Integer level, String[] FSys) {
        String addOp;
        if (Arrays.asList("plus", "minus").contains(SYM)) { //表达式可能会由加号或减号开始，表示正负号
            addOp = SYM; //保存正负号
            getToken();
            term(level, ArrayUtils.addAll(FSys, exprBegSys));
            if (addOp.equals("minus")) {
                gen("OPR", 0, 1); //生成1号操作指令：取反运算
            }
        } else {
            term(level, ArrayUtils.addAll(FSys, exprBegSys));
            while (Arrays.asList("plus", "minus").contains(SYM)) { //项后应是加运算或减运算
                addOp = SYM;
                getToken();
                term(level, ArrayUtils.addAll(FSys, exprBegSys));
                if (addOp.equals("plus")) {
                    gen("OPR", 0, 2); //生成2号操作指令：加运算
                } else {
                    gen("OPR", 0, 3); //生成3号操作指令：减运算
                }
            }
        }
    }

    /**
     * 条件处理
     * <条件> ::= <表达式><关系运算符><表达式> | ODD<表达式>
     * <关系运算法> ::= = | # | < | <= | > | >=
     *
     * @param level 项分析所在的层次
     * @param FSys  用于出错恢复的单词集合
     */
    private void condition(Integer level, String[] FSys) {
        String relOp;
        if (SYM.equals("oddSYM")) { //odd运算符(一元)
            getToken();
            expression(level, FSys); //对odd的表达式进行处理计算
            gen("OPR", 0, 6); //生成6号操作指令：奇偶判断运算
        } else { //不是odd运算符，定是二元逻辑运算符
            expression(level, ArrayUtils.addAll(FSys, condBegSys)); //对表达式左部进行处理计算
            if (!Arrays.asList(condBegSys).contains(SYM)) { //token不是关系运算符
                errors.add(token.getROW() + ": " + Error.log(20)); //应为关系运算符
            } else {
                relOp = SYM; //保存当前的关系运算符
                getToken();
                expression(level, FSys); //对表达式右部进行处理计算
                switch (relOp) {
                    case "eql" -> gen("OPR", 0, 8);
                    case "neq" -> gen("OPR", 0, 9);
                    case "lss" -> gen("OPR", 0, 10);
                    case "geq" -> gen("OPR", 0, 11);
                    case "gtr" -> gen("OPR", 0, 12);
                    case "leq" -> gen("OPR", 0, 13);
                }
            }
        }
    }

    /**
     * 项处理
     * <项> ::= <因子>{ <乘法运算符}><因子> }
     * <乘法运算符> ::= * | /
     *
     * @param level 项分析所在的层次
     * @param FSys  用于出错恢复的单词集合
     */
    private void term(Integer level, String[] FSys) {
        String mulOp;
        factor(level, ArrayUtils.addAll(FSys, termBegSys)); //每一个项都应该由因子开始
        while (Arrays.asList("times", "slash").contains(SYM)) { //一个因子后应当遇到乘号或除号
            mulOp = SYM; //保存当前运算符
            getToken();
            factor(level, ArrayUtils.addAll(FSys, termBegSys)); //乘除运算符后应是因子
            if (mulOp.equals("times")) { //*
                gen("OPR", 0, 4); //生成乘法指令
            } else {
                gen("OPR", 0, 5); //生成除法指令
            }
        }
    }

    /**
     * 因子处理
     * <因子> ::= <标识符> | <无符号整数> | '('<表达式>')'
     *
     * @param level 因子分析所在的层次
     * @param FSys  用于出错恢复的单词集合
     */
    private void factor(Integer level, String[] FSys) {
        Integer i;
        test(facBegSys, FSys, 24); //开始因子处理前，先检查当前token是否在facBegSys集合中
        while (Arrays.asList(facBegSys).contains(SYM)) { //循环处理因子
            switch (SYM) {
                case "identifier" -> { //遇到的是标识符
                    i = position(ID); //查声明表，找到当前标识符在声明表中的位置
                    if (i == 0) { //如果查声明表返回为0，表示没有找到标识符
                        errors.add(token.getROW() + ": " + Error.log(11)); //标识符未说明
                    } else {
                        Declaration declaration = declarations.get(i);
                        switch (declaration.getKind()) {
                            case "constSym" -> gen("LIT", 0, declaration.getValue()); //生成LIT指令，将常量值放到栈顶
                            case "varSym" -> gen("LOD", level - declaration.getLevel(), declaration.getAddress()); //吧位于距离当前层level的层的偏移地址为a的变量放到栈顶
                            case "procedureSym" -> errors.add(token.getROW() + ": " + Error.log(21)); //表达式内标识符属性不能是过程
                        }
                    }
                    getToken();
                }
                case "number" -> { //遇到的是数字
                    if (NUM > NumMAx) {
                        errors.add(token.getROW() + ": " + Error.log(31)); //数越界
                        NUM = 0; //将数字按0值处理
                    }
                    gen("LIT", 0, NUM); //生成LIT指令，将NUM放到栈顶
                    getToken();
                }
                case "lparen" -> { //遇到的是左括号
                    getToken();
                    expression(level, ArrayUtils.addAll(FSys, "rparen".split("@"))); //递归调用expression子程序分析子表达式
                    if (SYM.equals("rparen")) { //子表达式分析完后，应遇到右括号
                        getToken();
                    } else errors.add(token.getROW() + ": " + Error.log(22)); //表达式中漏掉右括号)
                }
            }
        }
        test(FSys, facBegSys, 23);
    }


    /**
     * 常量定义处理
     *
     * @param level 语法分析所在的层次
     * @param dx    数据段内存分配指针，指向下一个被分配空间在数据段中的偏移位置
     * @return 更新dx
     */
    private Integer constDeclaration(Integer level, Integer dx) {
        if (SYM.equals("identifier")) { //常量声明过程开始遇到的第一个符号必然应为标识符
            getToken();
            if (SYM.equals("eql") || SYM.equals("becomes")) {
                if (SYM.equals("becomes")) { //如果是赋值号(常量生明中应该是等号
                    errors.add(token.getROW() + ": " + Error.log(1)); //常数说明中的=写成:=
                }
                getToken();
                if (SYM.equals("number")) {
                    dx = enter(level, dx, "constSym");
                    getToken();
                } else {
                    errors.add(token.getROW() + ": " + Error.log(2)); //常数说明中的=后应是数字
                }
            } else {
                errors.add(token.getROW() + ": " + Error.log(3)); //常数说明中的标识符后应是=
            }
        } else {
            errors.add(token.getROW() + ": " + Error.log(4)); //应是标识符
        }
        return dx;
    }

    /**
     * 变量定义处理
     *
     * @param level 语法分析所在的层次
     * @param dx    数据段内存分配指针，指向下一个被分配空间在数据段中的偏移位置
     * @return 更新dx
     */
    private Integer varDeclaration(Integer level, Integer dx) {
        if (SYM.equals("identifier")) { //常量声明过程开始遇到的第一个符号必然应为标识符
            dx = enter(level, dx, "varSym");
            getToken();
        } else {
            errors.add(token.getROW() + ": " + Error.log(4)); //应是标识符
        }
        return dx;
    }

    /**
     * 对目标代码的解释执行程序
     */
    private void interpret() {
        Integer P; //程序地址寄存器，指向下一条要要执行的目标指令的地址(相当于codes数组的下标)
        int B; //基址寄存器，指向每个过程被调用时，在数据区S中分配给它的局部变量数据段起始地址
        int T; //栈顶寄存器，类PCode是在一种假想的栈式计算上运行的，T记录当前栈顶位置
        Code I; //指令寄存器，存放着当前正在运行的一条目标指令
        System.out.println("PL/0程序开始运行。。。");
        T = 0; //程序开始运行时栈顶寄存器置010
        B = 1; //数据段基址置0
        P = 0; //从0号代码开始执行程序
        //数据内存中为SL,DL,RA三个单元均为0，表示为主程序
        S[0] = 0;
        S[1] = 0;
        S[2] = 0;
        S[3] = 0;
        do {
            I = codes.get(P); //获取一条目标代码
            P++; //指令指针加一，指向下一条代码
            switch (I.getFunction()) {
                case "LIT":
                    T++; //栈顶指令上移，在栈中分配了一个单元
                    S[T] = I.getAddressOffset(); //该单元的内容存放I指令的a操作数，即实现了吧常量值放到运行栈栈顶
                    break;
                case "OPR":
                    switch (I.getAddressOffset()) {
                        //0号操作：从子程序返回操作
                        case 0 -> {
                            T = B - 1; //释放这段子程序占用的数据内存空间
                            P = S[T + 3]; //把指令指针取到RA的值，指向的是返回地址
                            B = S[T + 2]; //把数据段基址取到DL的值，指向调用前子过程的数据段基址
                        }
                        //1号操作：栈顶数据取反操作
                        case 1 -> S[T] = -S[T];
                        //2号操作：栈顶两个数据加法操作
                        case 2 -> {
                            T--; //栈顶指针下移
                            S[T] = S[T] + S[T + 1]; //两单元数据相加存入栈顶
                        }
                        //3号操作：栈顶两个数据减法操作(次栈顶 - 栈顶)
                        case 3 -> {
                            T--; //栈顶指针下移
                            S[T] = S[T] - S[T + 1]; //两单元数据相减存入栈顶
                        }
                        //4号操作：栈顶两个数据乘法操作
                        case 4 -> {
                            T--; //栈顶指针下移
                            S[T] = S[T] * S[T + 1]; //两单元数据相乘存入栈顶
                        }
                        //5号操作：栈顶两个数据除法操作(次栈顶 div 栈顶)
                        case 5 -> {
                            T--; //栈顶指针下移
                            S[T] = S[T] / S[T + 1]; //两单元数据相整除存入栈顶
                        }
                        //6号操作：判奇操作
                        case 6 -> S[T] = (S[T] % 2);
                        //8号操作：栈顶两个数据判等操作
                        case 8 -> {
                            T--; //栈顶指针下移
                            S[T] = (S[T] == S[T + 1] ? 1 : 0); //判等，相等栈顶置1，不等置0
                        }
                        //9号操作：栈顶两个数据判不等操作
                        case 9 -> {
                            T--; //栈顶指针下移
                            S[T] = (S[T] == S[T + 1] ? 0 : 1); //判等，不等栈顶置1，相等置0
                        }
                        //10号操作：栈顶两个数据判小于操作(次栈顶 < 栈顶)
                        case 10 -> {
                            T--; //栈顶指针下移
                            S[T] = (S[T] < S[T + 1] ? 1 : 0); //判小于，如果次栈顶小于栈顶，栈顶置1，否则置0
                        }
                        //11号操作：栈顶两个数据判大于等于操作(次栈顶 >= 栈顶)
                        case 11 -> {
                            T--; //栈顶指针下移
                            S[T] = (S[T] >= S[T + 1] ? 1 : 0); //判大于等于，如果次栈顶大于等于栈顶，栈顶置1，否则置0
                        }
                        //12号操作：栈顶两个数据判大于操作(次栈顶 > 栈顶)
                        case 12 -> {
                            T--; //栈顶指针下移
                            S[T] = (S[T] > S[T + 1] ? 1 : 0); //判大于，如果次栈顶大于栈顶，栈顶置1，否则置0
                        }
                        //13号操作：栈顶两个数据判小于等于操作(次栈顶 <= 栈顶)
                        case 13 -> {
                            T--; //栈顶指针下移
                            S[T] = (S[T] <= S[T + 1] ? 1 : 0); //判小于等于，如果次栈顶小于等于栈顶，栈顶置1，否则置0
                        }
                        //14号操作：输出栈顶值操作
                        case 14 -> {
                            System.out.print("栈顶值为：" + S[T]);
                            T--;
                        }
                        //15号操作：输出换行操作
                        case 15 -> System.out.print("\n");
                        //16号操作：接受键盘值输入到栈顶
                        case 16 -> {
                            T++; //栈顶上移，分配空间
                            System.out.print("请输入值：");
                            Scanner input = new Scanner(System.in);
                            S[T] = input.nextInt();
                        }
                    }
                    break;
                case "LOD":
                    T++; ////栈顶上移，分配空间
                    //通过数据区层差和偏移地址a找到变量的数据，存入栈顶
                    S[T] = S[base(B, I.getLevelDifference()) + I.getAddressOffset()];
                    break;
                case "STO":
                    S[base(B, I.getLevelDifference()) + I.getAddressOffset()] = S[T];
                    T--;
                    break;
                case "CAL":
                    S[T + 1] = base(B, I.getLevelDifference()); //在栈顶压入静态链SL
                    S[T + 2] = B; //然后压入当前数据区基址，作为动态链DL
                    S[T + 3] = P; //最后压入当前的断电，作为返回地址RA
                    //以上的工作即为过程调用前的保护现场
                    B = T + 1; //将当前数据区基址指向SL所在位置
                    P = I.getAddressOffset(); //a所指位置开始继续执行指令，即实现了程序执行的跳转
                    break;
                case "INT":
                    T = T + I.getAddressOffset(); //栈顶上移a个空间，即开辟a个新的内存单元
                    break;
                case "JMP":
                    P = I.getAddressOffset();
                    break;
                case "JPC":
                    if (S[T] == 0) { //判断栈顶值
                        P = I.getAddressOffset(); //如果是0就跳转，否则不跳转
                    }
                    T--;
                    break;
            }
        } while (P != 0); //如果P等于0，意味着在主程序运行时遇到了从子程序返回指令，也就是整个程序运行的结束
        System.out.println("PL/0程序完成运行");
    }

    /**
     * 通过静态链求出数据区基地址的函数base
     *
     * @param B 当前层的基址
     * @param L 要求的数据区所在层与当前层的层差
     * @return 要求的数据区基址
     */
    private Integer base(Integer B, Integer L) {
        while (L > 0) { //如果 L 大于 0，循环通过静态链往前找需要的数据区基址
            B = S[B]; //用当前层数据区基址中的内容(正好是静态链SL数据，为上一层的基址)的作为新的当前层，即向栈底找了一层
            L--;
        }
        return B;
    }

    /**
     * 登录符号表过程enter
     *
     * @param sym 欲登录到符号表的符号类型
     */
    private Integer enter(Integer level, Integer dx, String sym) {
        Declaration declaration = new Declaration(ID, sym);
        switch (sym) {
            case "constSym" -> {
                if (NUM > NumMAx) {
                    errors.add(token.getROW() + ": " + Error.log(31)); //数越界
                    NUM = 0;
                }
                declaration.setValue(NUM);
            }
            case "varSym" -> {
                declaration.setLevel(level);
                declaration.setAddress(dx);
                dx++;
            }
            case "procedureSym" -> declaration.setLevel(level);
        }
        declarations.add(declaration);
        return dx;
    }

    /**
     * 查找标识符在声明表中的位置
     *
     * @param id 目标符号
     * @return 目标符号在符号表中的位置，如果找不到就返回0
     */
    private Integer position(String id) {
        Declaration declaration = declarations.get(0);
        declaration.setName(id); //将id放入声明表0号位置
        int i = declarations.size() - 1; //从声明表中当前位置也即最后一个符号开始找
        while (!declarations.get(i).getName().equals(id)) i--;
        return i;
    }

    /**
     * 测试当前单词符是否合法
     *
     * @param s1 当语法分析进入或退出某一语法单元时当前单词符合应属于的集合
     * @param s2 在某一出错状态下，可恢复语法分析正常工作的补充单词集合
     * @param n  出错信息编号，当当前符号不属于合法的s1集合时发出的出错信息
     */
    private void test(String[] s1, String[] s2, Integer n) {
        if (!Arrays.asList(s1).contains(SYM)) { //当前符号不在s1中
            errors.add(token.getROW() + ": " + Error.log(n));
            s1 = ArrayUtils.addAll(s1, s2); //把s2集合补充进s1集合
            while (!Arrays.asList(s1).contains(SYM)) getToken(); //通过循环找到下一个合法的符号，以恢复语法分析工作
        }
    }

    /**
     * 列出当前一层目标代码
     *
     * @param cx0 当前层开始位置指针
     */
    private void listCode(Integer cx0) {
        Code code;
        do {
            code = codes.get(cx0);
            System.out.println(cx0 + ": " + code.toString());
            cx0++;
        } while (cx0 < codes.size());
    }

    /**
     * 生成目标代码，并送入目标程序区
     *
     * @param func  功能码
     * @param level 层次差，即变量或过程被引用的分程序与说明该变量或过程的分程序之间的层次查
     * @param a     可以是常数值，位移量，操作符代码
     */
    private void gen(String func, Integer level, Integer a) {
        if (codes.size() > CXMax) { //当前生成的代码行号大于允许的最大代码行数
            System.out.println("程序过长。。。");
            //停止编译
            System.exit(0);
        } else {
            codes.add(new Code(func, level, a)); //生成一行代码
        }
    }

    /**
     * 获取下一个单词，并提取token中非空的数据
     * 目的: 可保留部分先前单词的数据
     * eg: a := 10 当读取到10时，SYM = "number", ID = "a", NUM = 10,可简化enter()
     */
    private void getToken() {
        token = lexicalAnalyzer.getSym();
        tokens.add(token);
        if (token.getSYM() != null) SYM = token.getSYM();
        if (token.getID() != null) ID = token.getID();
        if (token.getNUM() != null) NUM = token.getNUM();
    }
}
