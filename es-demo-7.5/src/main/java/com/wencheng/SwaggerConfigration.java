package com.wencheng;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author : 唐逸
 * @version : created date: 2020/1/7 9:52
 */
@Configuration
@EnableSwagger2
public class SwaggerConfigration {

    /**
     * @return springfox.documentation.spring.web.plugins.Docket
     * @author : 唐逸
     * @description : 创建Api应用
     * @date : 2020/1/7
     */
    @Bean
    public Docket createAip() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wencheng.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * @return springfox.documentation.service.ApiInfo
     * @author : 唐逸
     * @description : 创建该API的基本信息,访问地址：http://localhost:8080/es/swagger-ui.html
     * @date : 2020/1/7
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("es-demo-7.5接口")
                .description("123456")
                .termsOfServiceUrl("http://www.baidu.com")
                .version("1.0.0")
                .build();
    }
}
