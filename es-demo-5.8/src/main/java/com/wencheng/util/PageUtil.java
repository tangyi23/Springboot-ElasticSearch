package com.wencheng.util;

import org.springframework.data.domain.Page;

/**
 * 
 * @author : 唐逸
 * @version : created date: 2019/12/17 14:07
 */
public class PageUtil {
    public static <T> PageResult<T> toPagedResult(Page<T> page) {
        PageResult<T> pageResult = new PageResult<T>();

        pageResult.setPageNo(page.getNumber());

        pageResult.setPageSize(page.getSize());

        pageResult.setPageCount(page.getTotalPages());

        pageResult.setTotalCount(page.getTotalElements());

        pageResult.setHasNextPage(!page.isLast());

        pageResult.setHasPreviousPage(!page.isFirst());

        pageResult.setData(page.getContent());

        return pageResult;
    }
}
