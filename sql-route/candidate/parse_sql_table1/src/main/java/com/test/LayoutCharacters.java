package com.test;

/**
 * Created by ben on 2016/2/5.
 */
public interface LayoutCharacters {

    /**
     * Tabulator column increment.
     */
    final static int  TabInc = 8;

    /**
     * Tabulator character.
     */
    final static byte TAB    = 0x8;

    /**
     * Line feed character.
     */
    final static byte LF     = 0xA;

    /**
     * Form feed character.
     */
    final static byte FF     = 0xC;

    /**
     * Carriage return character.
     */
    final static byte CR     = 0xD;

    /**
     * QS_TODO 为什么不是0x0？<br/>
     * End of input character. Used as a sentinel to denote the character one beyond the last defined character in a
     * source file.
     */
    final static byte EOI    = 0x1A;
}

