package com.leyou.order.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.security.Key;
import java.util.Date;

/**
 * ClassName: OrderSecKill <br/>
 * Description: TODO
 * Date 2020/5/31 20:54
 *
 * @author Lenovo
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_seckill_order")
public class SeckillOrder {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    private Long userId;

    private Long skuId;

    private Date createTime;

}
