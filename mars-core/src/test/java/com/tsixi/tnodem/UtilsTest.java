package com.tsixi.tnodem;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void test() {
		// get name representing the running Java virtual machine.  
		String name = ManagementFactory.getRuntimeMXBean().getName();  
		System.out.println(name);  
		// get pid  
		String pid = name.split("@")[0];  
		System.out.println("Pid is:" + pid); 
	}

}
