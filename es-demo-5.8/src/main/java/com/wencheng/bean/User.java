package com.wencheng.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author : 唐逸
 * @version : created date: 2019/12/16 13:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document(indexName = "user",type = "doc")
public class User implements Serializable {

    @Id
    private Integer id;

    //@Field(type = FieldType.Text, analyzer = "ik_smart_pinyin")
    private String name;

    //@Field(type = FieldType.Integer)
    private Integer age;

    //@Field(type = FieldType.Date)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
}
