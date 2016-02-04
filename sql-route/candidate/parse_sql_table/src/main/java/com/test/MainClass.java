package com.test;

import java.util.HashSet;
import java.util.List;

/**
 * Created by ben on 2016/2/4.
 */
public class MainClass {


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println(
                    "Usage: " + SqlTableParser.class.getSimpleName() + " <sql>"
            );
        }

        String sql = args[0];

        SqlTableParser parser = new SqlTableParser();

        //String sql = _sql2;
        try {
            List<String> tables = parser.parse(sql);

            printTable(tables);
        } catch (Exception e) {
            System.err.println(
                    "Error: " + e.getMessage()
            );
        }
    }

    private static void printTable(List<String> tables) throws Exception {
        for (String s : tables) {
            System.out.println(s);
        }
    }
}
