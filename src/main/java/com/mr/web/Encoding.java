package com.mr.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class Encoding implements Filter { 
	private String encoding="UTF-8";
	private FilterConfig fiterConfig=null;

	public void destroy() { 
	} 

	public void doFilter(ServletRequest request, ServletResponse response,
		    FilterChain filterChain) throws IOException, ServletException {
		
		   this.encoding =fiterConfig.getInitParameter("encoding");
		   if(this.encoding==null){
			   encoding="UTF-8"; 
		   }
		   request.setCharacterEncoding(this.encoding);
		   response.setCharacterEncoding(this.encoding);
		   filterChain.doFilter(request, response);
		}

	public void init(FilterConfig filterConfig) throws ServletException { 
		this.fiterConfig=filterConfig;
	} 
}  