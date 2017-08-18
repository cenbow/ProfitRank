package com.mr.util;

import org.codehaus.jackson.map.ObjectMapper;

public class JacksonUtil {

	private JacksonUtil() {
	}

	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 
	 * @return
	 */
	public static ObjectMapper getInstance() {

		return mapper;
	}
}
