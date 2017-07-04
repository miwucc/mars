/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2017年3月1日 	
 */
package com.tsixi.alan;

import com.csvreader.CsvReader;
import info.monitorenter.cpdetector.CharsetPrinter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 *
 * 
 * @since 1.0
 * 
 * @author Alan
 *
 */
public class MyTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        CharsetPrinter charsetPrinter = new CharsetPrinter();

        File file = new File("E:\\miner\\DesignFile\\config\\prop.csv");

        String charset = charsetPrinter.guessEncoding(file);
        System.out.println(charset);

        CsvReader reader = new CsvReader(file.getPath(),'\t', Charset.forName(charset));
        while (reader.readRecord()) {
            String str = reader.getRawRecord();
            String[] values = reader.getValues();
            Arrays.asList(values).forEach(s -> System.out.print(s+"_"));
            System.out.println();
        }
        reader.close();
    }

}
