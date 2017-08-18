package com.mr.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.mr.util.JacksonWriteUtil;



public class BaseController {

	protected String basePath;

	public BaseController() {
	}

	protected String view(String path) {
		return this.basePath + path;
	}
	
   protected void outJson(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("text/plain");
        response.getWriter().println(JacksonWriteUtil.getInstance().writeValueAsString(object));
    }
	
   protected void outJson(HttpServletResponse response, Object object,ObjectMapper mapper) throws IOException {
       response.setContentType("text/plain");
       response.getWriter().println(mapper.writeValueAsString(object));
   }
	
   protected void outJson(HttpServletResponse response,String str) throws IOException {
       response.setContentType("text/plain");
       response.getWriter().println(str);
   }
   

}
