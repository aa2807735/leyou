package com.leyou.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ClassName: SpechParam <br/>
 * Description: TODO
 * Date 2020/4/30 13:06
 *
 * @author Lenovo
 **/
@Data
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    //NUMERIC 是mysql的关键字，加上·变成普通字符串
    @Column(name = "`numeric`")
    private Boolean numeric;
    private Boolean generic;
    private String unit;
    private Boolean searching;
    private String segments;
}
