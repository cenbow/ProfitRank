package com.mr.util;

import java.util.Comparator;

import com.mr.datagather.bean.Rate;

public class OrderComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		
		Rate r1 = (Rate) o1; // 强制转换  
		Rate r2 = (Rate) o2;  
        return new Integer(r1.getOrder()).compareTo(new Integer(r2.getOrder())); 
	}

	
}
