package com.wencheng.controller;

import com.wencheng.bean.User;
import com.wencheng.service.EsService;
import com.wencheng.util.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * @Title : UserController
 * @Package : com.wencheng.controller
 * @Description :
 * @author : 唐逸
 * @date : 2020/1/7 16:37
 */
@Api("User接口")
@RestController
public class UserController {

    private static final  Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private EsService esService;

    @Autowired
    public UserController(EsService esService){
        this.esService = esService;
    }

    /**
     * @author : 唐逸
     * @description : 添加索引
     * @date : 2020/1/7
     * @param index
     * @return java.lang.String
     */
    @RequestMapping(value = "/createIndex", method = RequestMethod.PUT)
    @ApiOperation(value = "添加索引", notes = "输入索引名")
    @ApiImplicitParam(paramType = "query", name = "index",value = "索引名", dataType = "String", required = true)
    public String createIndex(String index){
        try{
            esService.createIndex(index);
            return "添加成功";
        }catch (Exception e){
            e.printStackTrace();
            return "添加失败";
        }
    }

    /**
     * @author : 唐逸
     * @description : 删除索引
     * @date : 2020/1/7
     * @param index
     * @return java.lang.String
     */
    @RequestMapping(value = "/delIndex", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除索引", notes = "输入索引名")
    @ApiImplicitParam(paramType = "query", name = "index",value = "索引名", dataType = "String", required = true)
    public String delIndex(String index){
        try{
            return esService.delIndex(index);
        }catch (Exception e){
            e.printStackTrace();
            return "删除失败";
        }
    }
    /**
     * @author : 唐逸
     * @description : 分页获取全部
     * @date :2020/1/7
     * @param page 当前页
     * @param size 每页最大数据量
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @RequestMapping(value = "/getAll/{page}/{size}", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据", notes = "根据分页获取")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "path", name = "size", value = "每页最大数据量", dataType = "int", required = true)
    })
    public PageResult<User> getAll(@PathVariable Integer page, @PathVariable Integer size){

        return esService.getAll(page, size);
    }

    /**
     * @author : 唐逸
     * @description : 添加数据
     * @date : 2020/1/7
     * @param sum 添加的数据量
     * @param name 名称
     * @return java.lang.String
     */
    @RequestMapping(value = "/save/{id}/{sum}", method = RequestMethod.PUT)
    @ApiOperation(value = "添加数据", notes = "根据名称和指定的数量添加")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "id", value = "id", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "path", name = "sum", value = "添加的数量", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "query", name = "name", value = "名称", dataType = "String", required = true)
    })
    public String save(String name, @PathVariable Integer sum, @PathVariable Integer id){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            System.out.println("<<<<<添加数据--开始>>>>>");
            for (int i = id; i <= sum+id; i++) {
                User user = new User(i,name+i,10+i, simpleDateFormat.parse("2019-9-23"));
                System.out.println(user);
                esService.save(user);
            }
            System.out.println("<<<<<添加数据--结束>>>>>");
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return "<<<<<添加失败>>>>>";
        }
        return "<<<<<添加成功>>>>>";
    }

    /**
     * @author : 唐逸
     * @description : 按id删除
     * @date : 2020/1/7
     * @param id id
     * @return java.lang.String
     */
    @RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据", notes = "根据id删除")
    @ApiImplicitParam(paramType = "path", name = "id", value = "唯一标识", required = true)
    public String del(@PathVariable Integer id){

        try {
            esService.delById(id);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return "删除失败";
        }
        return "删除成功";
    }

    /**
     * @author : 唐逸
     * @description :
     * @date : 2020/1/7
     * @param page
     * @param size
     * @param criteria
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @RequestMapping(value = "/findByNameLike/{page}/{size}", method = RequestMethod.GET)
    @ApiOperation(value = "根据姓名模糊查询", notes = "请输入姓名")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "path", name = "size", value = "每页最大数据量", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "query", name = "criteria", value = "条件", dataType = "String", required = true)
    })
    public PageResult<User> findByNameLike(@PathVariable Integer page, @PathVariable Integer size, String criteria){
        return esService.findByNameLike(page, size, criteria);
    }

    /**
     * @author : 唐逸
     * @description : ElasticsearchTemplate全文搜索
     * @date : 22020/1/7
     * @param page
     * @param size
     * @param criteria
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.GET)
    @ApiOperation(value = "全文搜索", notes = "输入条件")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "page", value = "当前页", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "path", name = "size", value = "每页最大数据量", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "query", name = "criteria", value = "条件", dataType = "String", required = true)
    })
    public PageResult<User> search(@PathVariable Integer page, @PathVariable Integer size, String criteria){
        if (StringUtils.isEmpty(page))
            page = 0;
        if (StringUtils.isEmpty(size))
            size = 10;
        return esService.search(page, size, criteria);
    }

    /**
     * @author : 唐逸
     * @description :
     * @date : 2020/1/7
     * @param page
     * @param size
     * @param name
     * @param age
     * @param start
     * @param end
     * @return com.wencheng.util.PageResult<com.wencheng.bean.User>
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "多条件搜素", notes = "输入多个条件")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", name = "page", value = "当前页", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "query", name = "size", value = "每页最大数据量", dataType = "int", required = true),
        @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", dataType = "String", required = false),
        @ApiImplicitParam(paramType = "query", name = "age", value = "年龄", dataType = "int", required = false),
        @ApiImplicitParam(paramType = "query", name = "start", value = "时间范围--开始", dataType = "String", required = false),
        @ApiImplicitParam(paramType = "query", name = "end", value = "时间范围--结束", dataType = "String", required = false)
    })
    public PageResult<User> search(Integer page, Integer size, String name, Integer age, String start, String end){
        if (StringUtils.isEmpty(page))
            page = 0;
        if (StringUtils.isEmpty(size))
            size = 10;
        return esService.search(page, size, name, age, start, end);
    }

    @Test
    public void test(){
        byte[] bytes = "blockslast_block".getBytes();
        System.out.println(Arrays.toString(bytes));

    }
}
