package com.mr.datagather.bean;

/**
 * Created by Administrator on 2017/8/9.
 */

import java.util.ArrayList;
import java.util.Collection;

/**
 * 表示分页中的一页。
 */
public class Page<E> {
    public static final int code_success = 0;
    public static final int code_fail = 1;
    private int code = 0;
    private String msg;
    private int startIndex;//当前页开始记录(起始为0)
    private long total;   // 总记录数
    private int pageSize = 30;  // 每页记录数
    private int curPage; // 当前页号

    private Collection<E> items;//当前页包含的记录列表

    private E item;



    /**
     * 取得页开始index
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static int getStartIdxOfPage(int pageNo, int pageSize) {
        return (pageNo-1) * pageSize;
    }



    public Page(int startIndex, long totalcount, int pagesize, Collection<E> list) {
        this.items = list;
        this.startIndex = startIndex;
        this.total = totalcount;

        if (pagesize > 0) {
            this.pageSize = pagesize;
        }

        this.curPage = (int) ((startIndex % pageSize == 0 && startIndex > 0) ? startIndex / pageSize + 1 : startIndex / pageSize + 1);

    }

    public Page() {
        this.items = new ArrayList<E>();

    }

    /**
     * @return the items
     */
    public Collection<E> getItems() {
        return items;
    }



    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }



    /**
     * @return the total
     */
    public long getTotal() {
        return total;
    }



    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }


    /**
     * @return the curPage
     */
    public int getCurPage() {
        return curPage;
    }


    public int getTotalPage() {
        return (int) ( (this.total%this.pageSize==0 && this.pageSize >0) ? this.total/this.pageSize : this.total/this.pageSize+1 );
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the item
     */
    public E getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(E item) {
        this.item = item;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }
}
