/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2016年8月2日 	
 */
package com.tsixi.tnodem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * 
 * @scene 1.0
 * 
 * @author Alan
 *
 */
public class ScriptTest {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			System.err.println("args is err,args=" + args);
			System.exit(0);
		} else if (args.length == 1) {
			System.out.print(exec(args[0]));
		} else {
			System.out.print(exec(args));
		}
	}

	/**
	 * 执行给定的命令
	 * 
	 * @param cmd
	 * @return
	 * @throws IOException
	 */
	public static String exec(String cmd) throws Exception {
		Process process = Runtime.getRuntime().exec(cmd);
		InputStream is = process.getInputStream();
		InputStream err = process.getErrorStream();
		return read(is) + read(err);
	}

	public static String exec(String[] cmd) throws Exception {
		Process process = Runtime.getRuntime().exec(cmd);
		InputStream is = process.getInputStream();
		InputStream err = process.getErrorStream();
		return read(is) + read(err);
	}

	public static String read(InputStream is) throws Exception {
		InputStreamReader inReader = new InputStreamReader(is, "utf-8");
		BufferedReader br = new BufferedReader(inReader);
		StringBuilder sb = new StringBuilder();
		char[] charBuffer = new char[1024];
		int n = 0;
		while ((n = br.read(charBuffer)) != -1) {
			sb.append(charBuffer, 0, n);
		}
		return sb.toString();
	}

}
