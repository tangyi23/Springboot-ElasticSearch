package com.wencheng.service;

import com.wencheng.bean.User;
import com.wencheng.util.PageResult;

/**
 * @Title : EsService
 * @Package : com.wencheng.service
 * @Description :
 * @author : 唐逸
 * @date : 2020/1/7 16:37
 */
public interface EsService {

    void delById(Integer id);

    PageResult<User> getAll(Integer page, Integer size);

    void save(User user);

    PageResult<User> findByNameLike(Integer page, Integer size, String criteria);

    PageResult<User> search(Integer page, Integer size, String criteria);

    PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end);

    void createIndex(String index);

    String delIndex(String index);
}
