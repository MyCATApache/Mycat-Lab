package com.test;

import java.util.*;

/**
 * Created by ben on 2016/2/3.
 */
public class SqlTableParser {

    private static final String select = "SELECT ";
    private static final String from = " FROM ";
    private static final String union = " UNION ";
    private static final String where = " WHERE ";
    private static final String groupBy = " GROUP BY ";
    private static final String orderBy = " ORDER BY ";
    private static final String join = " JOIN ";

    private HashSet<String> tables;
    private List<String> clauses;
    private Stack<Integer> bracketStack;

    public SqlTableParser() {
        tables = new HashSet<String>();
        clauses = new ArrayList<String>();
        bracketStack = new Stack<Integer>();
    }

    public List<String> parse(String sql) throws Exception {
        if (sql == null || sql.length() < 15) {
            throw new IllegalArgumentException("Invalid sql");
        }
        tables.clear();
        clauses.clear();
        clearStack();

        //换成大写
        String s = sql.toUpperCase();

        //把多个空格换成1个空格
        s = s.replaceAll("\\s+", " ");

        splitClause(s);

        for (String single : clauses) {
            parseSql(single);
        }

        return getList();
    }

    private void splitClause(String s) throws Exception {
        int length = s.length();
        int i2;
        String temp;

        char c;
        //按括号来分子查询
        for (int i1 = 0; i1 < length; i1++) {
            c = s.charAt(i1);
            if (c == '(') {
                //入栈
                bracketStack.push(i1);
            } else if (c == ')') {
                //出栈
                i2 = bracketStack.pop();
                temp = s.substring(i2 + 1, i1);
                //如果有select，说明是个子查询
                if (temp.indexOf(select) != -1) {
                    clauses.add(temp);
                    s = s.substring(0, i2 + 1) + s.substring(i1);
                    length = s.length();
                    i1 = i2 + 1;
                }
            }
        }

        //把最后一个子查询加入，如果没有括号，就把整个句子当一个子查询
        clauses.add(s);

        if (bracketStack.empty() == false) {
            throw new Exception("Invalid sql, from statement not match select statement");
        }
    }

    private void parseSql(String sql) throws Exception {
        //括号里的子查询可能会出现 select   union  select 的情况，要分成独自的select
        String temp;
        int startIdx = 0;
        int idx = sql.indexOf(union);
        while (idx != -1) {
            temp = sql.substring(0, idx);
            parseSingleSql(temp);
            sql = sql.substring(idx + 7);
            idx = sql.indexOf(union);
        }

        parseSingleSql(sql);
    }

    private void parseSingleSql(String sql) throws Exception {
        int afterFrom = sql.indexOf(from) + 6;
        if (afterFrom == 5) {
            //没找到from，这个select有问题啊
            throw new Exception("select 没有对应的from, sql: " + sql);
        }
        String temp = null;
        //完整的select 包含   from, where, group by, order by
        int whereIdx = sql.indexOf(where);
        int groupByIdx;
        int orderByIdx;
        if (whereIdx != -1) {
            //有where语句， where语句在group by和order by的前面，就不用管他们了
            temp = sql.substring(afterFrom, whereIdx);
        } else {
            groupByIdx = sql.indexOf(groupBy);
            if (groupByIdx != -1) {
                temp = sql.substring(afterFrom, groupByIdx);
            } else {
                orderByIdx = sql.indexOf(orderBy);
                if (orderByIdx != -1) {
                    temp = sql.substring(afterFrom, orderByIdx);
                }
            }
        }
        if (temp == null) {
            //没有where， group by，order by
            temp = sql.substring(afterFrom);
        }
        parseTable(temp);
    }

    private void parseTable(String sql) {
        //这里都是from后面的表， 要么是 逗号分隔，  要么是join 分隔，要么就1张表
        String temp = sql;
        int startIdx = 0;
        int commaIdx = sql.indexOf(',');
        List<String> tbs = new ArrayList<String>();
        //用逗号分隔
        if (commaIdx != -1) {
            while (commaIdx != -1) {
                tbs.add(sql.substring(0, commaIdx));
                sql = sql.substring(commaIdx + 1);
                commaIdx = sql.indexOf(',');
            }
        } else {
            //用join分隔，或者没有
            int joinIdx = sql.indexOf(join);
            if (joinIdx != -1) {
                while (joinIdx != -1) {
                    tbs.add(sql.substring(0, joinIdx));
                    sql = sql.substring(joinIdx + 6);
                    joinIdx = sql.indexOf(join);
                }
            }
        }
        tbs.add(sql);

        for (String s1 : tbs) {
            parseSingleTable(s1);
        }
    }

    private void parseSingleTable(String sql) {
        sql = sql.trim();
        if (sql.charAt(0) == '(') {
            return;
        }
        int blankIdx = sql.indexOf(' ');
        if (blankIdx == -1) {
            tables.add(sql);
        } else {
            tables.add(sql.substring(0, blankIdx));
        }
    }

    private void clearStack() {
        while (!bracketStack.isEmpty()) {
            bracketStack.pop();
        }
    }

    public List<String> getList() {
        List<String> list = new ArrayList<String>();
        for (String s : tables) {
            list.add(s);
        }
        return list;
    }
}
