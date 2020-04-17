package com.study.fldemo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public static void main(String[] args) {
        String price = "HK$15.00";
        int numberIndex = getNumberIndex(price);
        System.out.println(price.substring(0,numberIndex));
    }

    public static int getNumberIndex(String price) {
        for (int i = 0; i < price.length(); i++) {
            char c = price.charAt(i);
            if (Character.isDigit(c)) {
                return i;
            }
        }
        return 0;
    }
}