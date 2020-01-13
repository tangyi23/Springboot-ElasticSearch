package com.wencheng.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wencheng.bean.User;
import com.wencheng.service.EsService;
import com.wencheng.util.DateTimeUtil;
import com.wencheng.util.PageResult;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title : EsServiceImp
 * @Package : com.wencheng.service.imp
 * @Description :
 * @author : 唐逸
 * @date : 2020/1/7 16:38
 */
@Service
public class EsServiceImp implements EsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsServiceImp.class);
    private static final String USER = "user";
    private static final String USER1 = "user1";
    private static final String YMD = "yyyy-MM-dd";

    private RestHighLevelClient restHighLevelClient;
    private ObjectMapper objectMapper;

    @Autowired
    public EsServiceImp(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper){
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    /**
     * @author : 唐逸
     * @description : 分页查询
     * @date : 2020/1/7
     * @param page 当前页
     * @param size 页面大小
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @Override
    public PageResult<User> getAll(Integer page, Integer size) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        return getPageResult(page, size, searchSourceBuilder);
    }

    /**
     * @author : 唐逸
     * @description : 添加数据
     * @date : 2020/1/7
     * @param user document
     * @return void
     */
    @SuppressWarnings("unchecked")
    @Override
    public void save(User user) {
        Map<String, Object> map = objectMapper.convertValue(user, Map.class);
        IndexRequest indexRequest = new IndexRequest(USER1).id(user.getId().toString()).source(map);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author : 唐逸
     * @description : 删除数据
     * @date : 2020/1/7
     * @param id 主键
     * @return void
     */
    @Override
    public void delById(Integer id) {
        DeleteRequest deleteRequest = new DeleteRequest(USER1, id.toString());
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author : 唐逸
     * @description : 按姓名模糊查询
     * @date : 2020/1/7
     * @param page 当前页
     * @param size 页面大小
     * @param criteria 关键字
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @Override
    public PageResult<User> findByNameLike(Integer page, Integer size, String criteria) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name", criteria));
        return getPageResult(page, size, searchSourceBuilder);
    }



    /**
     * @author : 唐逸
     * @description : 全文检索
     * @date : 2020/1/8
     * @param page 当前页
     * @param size 页面大小
     * @param criteria 关键字
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @Override
    public PageResult<User> search(Integer page, Integer size, String criteria) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(criteria));
        return getPageResult(page, size, searchSourceBuilder);
    }

    /**
     * @author : 唐逸
     * @description : 多条件搜索
     * @date : 2020/1/8
     * @param page 当前页
     * @param size 页面大小
     * @param name 名称
     * @param age 年龄
     * @param start 开始时间
     * @param end 结束时间
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @Override
    public PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(name)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", name));
        }
        if(!StringUtils.isEmpty(age)){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("age", age));
        }
        start = dateDispose(start);
        end = dateDispose(end);
        boolQueryBuilder.must(QueryBuilders.rangeQuery("birthday").format(YMD).from(start).to(end));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.highlighter(getHighlightBuilder());
        return getPageResult(page, size, searchSourceBuilder);
    }

    /**
     * @author : 唐逸
     * @description : 添加索引
     * @date : 2020/1/10
     * @param index 索引名
     * @return void
     */
    @Override
    public void createIndex(String index) {
        CreateIndexRequest createIndexRequest= new CreateIndexRequest(index);

        //分词器start
        Map<String, Object> ik_smart_pinyin = new HashMap<>();
        ik_smart_pinyin.put("type", "custom");
        ik_smart_pinyin.put("tokenizer", "ik_smart");
        String[] filterArr = {"my_pinyin", "word_delimiter"};
        ik_smart_pinyin.put("filter", filterArr);
        Map<String, Object> ik_max_word_pinyin = new HashMap<>();
        ik_max_word_pinyin.put("type", "custom");
        ik_max_word_pinyin.put("tokenizer", "ik_max_word");
        ik_max_word_pinyin.put("filter", filterArr);
        Map<String, Object> analyzer = new HashMap<>();
        analyzer.put("ik_smart_pinyin", ik_smart_pinyin);

        analyzer.put("ik_max_word_pinyin", ik_max_word_pinyin);
        //分词器end

        //过滤器start
        Map<String, Object> my_pinyin = new HashMap<>();
        my_pinyin.put("type", "pinyin");
        my_pinyin.put("first_letter", "prefix");
        my_pinyin.put("padding_char", "");
        Map<String, Object> filter = new HashMap<>();
        filter.put("my_pinyin", my_pinyin);
        //过滤器end

        //settings start
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("filter", filter);
        analysis.put("analyzer", analyzer);
        Map<String, Object> map = new HashMap<>();
        map.put("analysis",analysis);
        map.put("number_of_shards", 5);
        map.put("number_of_replicas", 2);
        createIndexRequest.settings(map);
        //settings end

        //mappings start
        Map<String, Object> name = new HashMap<>();
        name.put("type", "text");
        name.put("analyzer", "ik_smart_pinyin");
        Map<String, Object> age = new HashMap<>();
        age.put("type", "integer");
        Map<String, Object> birthday = new HashMap<>();
        birthday.put("type", "date");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("age", age);
        properties.put("birthday", birthday);
        Map<String, Object> _source = new HashMap<>();
        _source.put("enabled", true);
        Map<String, Object> _doc = new HashMap<>();
        _doc.put("_source", _source);
        _doc.put("properties", properties);
        createIndexRequest.mapping(_doc);
        //mappings end

        try {
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author : 唐逸
     * @description : 删除索引
     * @date : 2020/1/7
     * @param index 索引名
     * @return java.lang.String
     */
    @Override
    public String delIndex(String index) {
        //判断索引是否存在
        if(!existsIndex(index)){
            return "索引不存在";
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            if(delete.isAcknowledged()){
                return "删除成功";
            }else{
                return "删除失败";
            }

        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "删除失败";
    }

    /**
     * @author : 唐逸
     * @description : 判断索引是否存在
     * @date : 2020/1/7
     * @param index 索引名
     * @return boolean
     */
    private boolean existsIndex(String index){
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @author : 唐逸
     * @description :查询
     * @date : 2020/1/8
     * @param page 当前页
     * @param size 页面大小
     * @param searchSourceBuilder 条件
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    private PageResult<User> getPageResult(Integer page, Integer size, SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(USER);
        //分页 start
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page-1)*size);
        //分页 end
        //排序 start
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));
        //排序 end
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //封装查询结果 start
            SearchHit[] hits = searchResponse.getHits().getHits();
            PageResult<User> pageResult = new PageResult<>();
            for (SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null){
                    Text[] texts = nameField.fragments();
                    StringBuilder name = new StringBuilder();
                    for (Text str : texts){
                        name.append(str);
                    }
                    sourceAsMap.put("name", name.toString());
                }
                pageResult.getData().add(objectMapper.convertValue(sourceAsMap, User.class));
            }
            pageResult.setPageNo(page);
            pageResult.setPageSize(size);
            pageResult.setTotalCount(searchResponse.getHits().getTotalHits().value);
            pageResult.setPageCount((long)Math.ceil(pageResult.getTotalCount()/(size+0.0)));
            pageResult.setHasNextPage(page < pageResult.getPageCount());
            pageResult.setHasPreviousPage(page > 1);
            //封装查询结果 end
            return pageResult;
        }catch (ConnectException a){
            LOGGER.error("未连接");
        }catch (RuntimeException b){
            b.printStackTrace();
            LOGGER.error("返回错误");
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author : 唐逸
     * @description : 高亮
     * @date : 2020/1/10
     * @return org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder
     */
    private HighlightBuilder getHighlightBuilder (){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        return highlightBuilder;
    }
    /**
     * @author : 唐逸
     * @description : 日期处理
     * @date : 2020/1/9
     * @param date 日期数据
     * @return java.lang.String
     */
    private String dateDispose(String date){
        if(StringUtils.isEmpty(date) || !DateTimeUtil.isDateStr(date, YMD)){
            return null;
        }
        return DateTimeUtil.parseDateToString(DateTimeUtil.parseStringToDate(date, YMD), YMD);
    }
}
