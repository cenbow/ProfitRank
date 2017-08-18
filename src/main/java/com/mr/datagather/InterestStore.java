package com.mr.datagather;

import com.mr.datagather.bean.Rate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InterestStore {

	public static Map<String,Rate> rateMap=new   ConcurrentHashMap<String,Rate>();
	
}
