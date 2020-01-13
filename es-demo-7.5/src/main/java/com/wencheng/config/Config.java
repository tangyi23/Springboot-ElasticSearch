package com.wencheng.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title : Config
 * @Package : com.wencheng.config
 * @Description :
 * @author : 唐逸
 * @date : 2020/1/7 16:36
 */
@Configuration
public class Config {

    private static final String HTTP = "http";

    @Value("#{'${elasticsearch.hosts}'.split(',')}")
    private  List<String> hosts;


    @Bean
    @Primary
    public RestHighLevelClient client(){
        ArrayList<HttpHost> arrHost = new ArrayList<HttpHost>();
        for (String host : hosts){
            String[] split = host.split(":");
            arrHost.add(new HttpHost(split[0], split.length == 2 ? Integer.parseInt(split[1]) : 9200, HTTP));
        }
        return new RestHighLevelClient(
                RestClient.builder(arrHost.toArray(new HttpHost[hosts.size()])));
    }

}
