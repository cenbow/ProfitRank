package com.mr.web.service;

/**
 * Created by Administrator on 2017/7/13.
 */
public interface PropertiesService {

    String getProperty(String key);

    String getProperty(String key, String defaultValue);
}
