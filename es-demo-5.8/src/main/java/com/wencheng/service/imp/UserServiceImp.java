package com.wencheng.service.imp;

import com.wencheng.bean.User;
import com.wencheng.dao.UserRepository;
import com.wencheng.service.UserService;
import com.wencheng.util.DateTimeUtil;
import com.wencheng.util.PageResult;
import com.wencheng.util.PageUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 *
 * @author : 唐逸
 * @version : created date: 2019/12/16 15:56
 */
@Service
public class UserServiceImp  implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PageResult<User> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageUser = userRepository.findAll(pageable);
        return PageUtil.toPagedResult(pageUser);
    }


    @Override
    public void save(User user) {
        userRepository.save(user);
    }


    @Override
    public PageResult<User> findByNameLike(Integer page, Integer size, String criteria) {
        Pageable pageable = PageRequest.of(page,size);
        Page<User> userPage = userRepository.findByNameLike(criteria, pageable);
        return PageUtil.toPagedResult(userPage);
    }


    @Override
    public void delById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public PageResult<User> search(Integer page, Integer size, String criteria) {
        System.out.println("条件>>>>>"+criteria);
        //分页
        Pageable pageable = PageRequest.of(page,size);
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                ;
        if (!StringUtils.isEmpty(criteria))
            searchQueryBuilder.withQuery(QueryBuilders.queryStringQuery(criteria));

        //搜索条件的入口
        SearchQuery searchQuery = searchQueryBuilder.build();
        Page<User> userPage = elasticsearchTemplate.queryForPage(searchQuery, User.class);
        return PageUtil.toPagedResult(userPage);
    }

    /**
     * @author : 唐逸
     * @description : 多条件搜索
     * @date : 2019/12/18
     * @param page
     * @param size
     * @param name
     * @param age
     * @param start
     * @param end
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @Override
    public PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end) {
        Pageable pageable = PageRequest.of(page, size);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (!StringUtils.isEmpty(name)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", name));
        }
        if (!StringUtils.isEmpty(age)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("age", age));
        }
        if (!StringUtils.isEmpty(start) && DateTimeUtil.isDateStr(start, "yyyy-MM-dd")){
            boolQueryBuilder.must(QueryBuilders.rangeQuery("birthday").format("yyyy-MM-dd").gt(start));
        }
        if (!StringUtils.isEmpty(end) &&  DateTimeUtil.isDateStr(end, "yyyy-MM-dd")){
            boolQueryBuilder.must(QueryBuilders.rangeQuery("birthday").format("yyyy-MM-dd").lt(end));
        }
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(boolQueryBuilder)
                .build();
        Page<User> userPage = elasticsearchTemplate.queryForPage(searchQuery, User.class);
        return PageUtil.toPagedResult(userPage);
    }
}
