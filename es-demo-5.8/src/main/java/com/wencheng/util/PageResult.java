package com.wencheng.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author : 唐逸
 * @version : created date: 2019/12/17 11:41
 */
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = -7327158695003901796L;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private long pageNo;//当前页

    private int pageSize ;//没页显示的数量

    private List<T> data;// 返回list数据

    private long totalCount; // 返回总的条数

    private long pageCount;//总页面数目

    private boolean isHasNextPage;//获取下一页

    private boolean isHasPreviousPage;//获取前一页

    public PageResult(long totalCount, int pageSize, List<T> data){
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.data = data;
    }

    public PageResult(){
        this(0L, DEFAULT_PAGE_SIZE, ((List<T>) (new ArrayList<T>())));
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isHasNextPage() {
        return isHasNextPage;
    }

    public void setHasNextPage(boolean isHasNextPage) {
        this.isHasNextPage = isHasNextPage;
    }

    public boolean isHasPreviousPage() {
        return isHasPreviousPage;
    }

    public void setHasPreviousPage(boolean isHasPreviousPage) {
        this.isHasPreviousPage = isHasPreviousPage;
    }
}
