package com.mr.util;

import com.mr.datagather.bean.Rate;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/6/14.
 */
public class RateComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        Rate r1 = (Rate) o1; // 强制转换
        Rate r2 = (Rate) o2;
        return r1.getRate().compareTo(r2.getRate());
    }
}
