package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.test.LayoutCharacters.EOI;
import static com.test.CharTypes.isFirstIdentifierChar;
import static com.test.CharTypes.isIdentifierChar;
import static com.test.CharTypes.isWhitespace;

/**
 * Created by ben on 2016/2/5.
 */
public class SqlTableParser {
    TreeSet<String> tables;
    private List<String> tables1;
    private final String text;
    private char ch;
    //一个词的开始位置
    private int mark;
    //当前读到的字符位置
    private int pos;
    private int bufPos;
    private String stringVal;
    private Token token;

    private Keywords keywods = Keywords.DEFAULT_KEYWORDS;

    public List<String> getTables() {
        if (tables1.size() == 0) {
            tables1 = getList();
        }
        return tables1;
    }

    private List<String> getList() {
        List<String> list = new ArrayList<String>();
        for (String s : tables) {
            list.add(s);
        }
        return list;
    }

    public SqlTableParser(String sql) throws Exception {
        text = sql;
        tables = new TreeSet<String>();
        tables1 = new ArrayList<String>();
        ch = charAt(0);
        try {
            parse();
        } catch (QuitException e) {
            String err = e.getMessage();
        }
    }

    public final char charAt(int index) throws Exception {
        if (index >= text.length()) {
            //throw new QuitException();
            return EOI;
        }

        return text.charAt(index);
    }

    public final void scanChar() throws Exception {
        ch = charAt(++pos);
        if (ch == EOI) {
            throw new QuitException();
        }
    }

    public final String subString(int offset, int count) {
        return text.substring(offset, offset + count);
    }

    public final String addSymbol() {
        return subString(mark, bufPos);
    }

    public String scanIdentifier() throws Exception {
        final char first = ch;

        if (ch == '`') {
            mark = pos;
            bufPos = 1;
            char ch;
            for (; ; ) {
                ch = charAt(++pos);

                if (ch == '`') {
                    bufPos++;
                    ch = charAt(++pos);
                    break;
                } else if (ch == EOI) {
                    throw new Exception("illegal identifier");
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = subString(mark, bufPos);
        } else {

            final boolean firstFlag = isFirstIdentifierChar(first);
            if (!firstFlag) {
                throw new Exception("illegal identifier");
            }

            mark = pos;
            bufPos = 1;
            char ch;
            for (; ; ) {
                //当最后一个字符
                try {
                    ch = charAt(++pos);
                } catch (QuitException e) {
                    break;
                }

                if (!isIdentifierChar(ch)) {
                    break;
                }

                bufPos++;
                continue;
            }

            this.ch = charAt(pos);

            stringVal = addSymbol();
        }
        return stringVal;
    }

    private void parse() throws Exception {
        //先把整个sql当做一个子查询
        handleClause();
    }

    private void handleClause() throws Exception {
        boolean firstWord = true;
        String identifier;
        for (; ; ) {
            ignoreWhitespace();

            //找到退出的的反括号了
            if (ch == ')') {
                scanChar();
                return;
            }

            //有括号，有可能是子查询
            if (ch == '(') {
                scanChar();
                handleClause();
                //第一个是子查询，所以已经不是第一了
                firstWord = false;
                continue;
            }

            //第一个单词如果不是select，就可以退出这个子查询了
            //Convert(Char(10), InspectDate, 20)
            //有个bug，当进入convert之后的（，会判断char不是select，所以就会去找），
            //但是char之后的（就会被忽略，所以10后的第一个）会被认为是退出的），就少了一层嵌套
            if (firstWord) {
                identifier = getIdentifier();
                if (identifier == null) {
                    //throw new Exception("有问题啊，以后解决");
                    identifier = String.valueOf(ch);
                }
                if (!"SELECT".equals(identifier.toUpperCase())) {
                    for (; ; ) {
                        if (ch == '(') {
                            scanChar();
                            handleClause();
                            continue;
                        }
                        if (ch == ')') {
                            scanChar();
                            break;
                        }
                        scanChar();
                    }
                    //退出子查询
                    return;
                }
                firstWord = false;
                continue;
            }

            //处理字符串
            if (ch == '\'') {
                handleLiteral();
                continue;
            }

            identifier = getIdentifier();
            if (identifier == null) {
                scanChar();
                continue;
            }

            if (!isFrom(identifier)) {
                continue;
            }

            //找到from啦,找到from之后，就要对from后面的内容进行处理
            if (handleFrom()) {
                break;
            }
        }
    }

    private void handleLiteral() throws Exception {
        for (; ; ) {
            scanChar();
            if (ch == '\'') {
                break;
            }
        }
        scanChar();
    }

    //如果在handlefrom的时候遇到了 ) 说明要退出子查询了
    private boolean handleFrom() throws Exception {
        String identifier;
        //from ( ) 当from后面第一table是子查询的时候，不能加到table里面
        boolean isClauseOut = false;
        for (; ; ) {
            ignoreWhitespace();

            //又见子查询
            if (ch == '(') {
                scanChar();
                handleClause();
                isClauseOut = true;
                continue;
            }

            ignoreWhitespace();

            //不是子查询，那就是表啊
            identifier = getIdentifier();
            if (identifier == null) {
                throw new Exception("咋又出问题了嘞，这里应该是表名");
            }

            //select * from (select * from b) as b1
            //from的第一个表如果是个子查询的话，就不算是表要跳过
            if (!isClauseOut) {
                tables.add(identifier);
            }

            for (; ; ) {
                //找到表名之后，看看后面还有其他表没,主要就是  , 和 join，
                // 如果遇到  where, group order,就要去找另一个from了
                ignoreWhitespace();
                //遇到反括号，子查询结束了
                if (ch == ')') {
                    scanChar();
                    return true;
                }
                //有逗号，说明有其他表
                if (ch == ',') {
                    scanChar();
                    break;
                }

                if (ch == '\'') {
                    handleLiteral();
                    continue;
                }

                identifier = getIdentifier();
                if (identifier == null) {
                    scanChar();
                    continue;
                }
                identifier = identifier.toUpperCase();
                if ("WHERE".equals(identifier)) {
                    return false;
                }
                if ("GROUP".equals(identifier)) {
                    return false;
                }
                if ("ORDER".equals(identifier)) {
                    return false;
                }
                if ("UNION".equals(identifier)) {
                    //遇到union了，退出重新找from
                    return false;
                }
                if (isJoin(identifier)) {
                    break;
                }
            }
            //同一个select， join之前应该没有 （，   只有join之后才会有（
            isClauseOut = false;
        }
    }

    private String getIdentifier() throws Exception {
        if (isFirstIdentifierChar(ch)) {
            if (ch == 'N') {
                if (charAt(pos + 1) == '\'') {
                    //处理 N字符串,就是把N跳过，然后当字符串处理
                    scanChar();
                    return null;
                }
            }

            return scanIdentifier();
        }
        return null;
    }

    private boolean isJoin(String identifier) {
        return "JOIN".equals(identifier.toUpperCase());
    }

    private boolean isFrom(String identifier) {
        return "FROM".equals(identifier.toUpperCase());
    }

    private void ignoreWhitespace() throws Exception {
        for (; ; ) {
            if (isWhitespace(ch)) {
                scanChar();
            } else {
                break;
            }
        }
    }
}
