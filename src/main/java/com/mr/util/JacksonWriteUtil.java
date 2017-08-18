package com.mr.util;

import org.codehaus.jackson.map.ObjectMapper;

public class JacksonWriteUtil {

	private JacksonWriteUtil() {
	}

	private static final ObjectMapper writemapper = new ObjectMapper();

	/**
	 * 
	 * @return
	 */
	public static ObjectMapper getInstance() {

		return writemapper;
	}
}
