package com.wencheng.util;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author : 唐逸
 * @version : created date: 2020/1/7 11:41
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = -7327158695003901796L;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private long pageNo;//当前页

    private int pageSize ;//没页显示的数量

    private List<T> data = new ArrayList<>();// 返回list数据

    private long totalCount; // 返回总的条数

    private long pageCount;//总页面数目

    private boolean isHasNextPage;//获取下一页

    private boolean isHasPreviousPage;//获取前一页


}
