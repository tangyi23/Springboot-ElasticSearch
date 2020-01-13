package com.wencheng.dao;

import com.wencheng.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author : 唐逸
 * @version : created date: 2019/12/16 14:22
 */
@Repository
public interface  UserRepository extends ElasticsearchRepository<User,Integer> {

    @Query("{\"match\":{\"name\":\"?0\"}}")
    public Page<User> findByNameLike(String name, Pageable pageable);

}
