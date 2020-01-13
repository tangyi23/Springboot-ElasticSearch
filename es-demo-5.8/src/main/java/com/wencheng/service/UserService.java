package com.wencheng.service;
import com.wencheng.bean.User;
import com.wencheng.util.PageResult;

/**
  *
  * @author : 唐逸
  * @version : created date: 2019/12/16 14:32
  */
public interface UserService {

    PageResult<User> getAll(int page, int size);

    PageResult<User> findByNameLike(Integer page, Integer size, String criteria);

    void save(User user);

    void delById(Integer id);

    PageResult<User> search(Integer page, Integer size, String criteria);

    PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end);
}
