package com.test;

import java.util.List;

/**
 * Created by ben on 2016/2/5.
 */
public class MainClass {

    public static void main(String[] args) throws Exception {
        String sql = "select a from a union select b from b";
        SqlTableParser parser = new SqlTableParser(sql);
        List<String> tables = parser.getTables();
        for (String t : tables) {
            System.out.println(t);
        }
    }
}
