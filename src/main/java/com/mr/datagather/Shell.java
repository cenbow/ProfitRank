package com.mr.datagather;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Shell {

	private static ClassPathXmlApplicationContext ctx;
	
	public static void main(String[] args) {

		ctx = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml");
		ctx.registerShutdownHook();
		ctx.start();
		
	}

}
