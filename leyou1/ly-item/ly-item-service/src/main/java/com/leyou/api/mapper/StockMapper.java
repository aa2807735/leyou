package com.leyou.api.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ClassName: StockMapper <br/>
 * Description: TODO
 * Date 2020/5/1 11:41
 *
 * @author Lenovo
 **/
public interface StockMapper extends BaseMapper<Stock> {
    @Update("UPDATE tb_stock SET stock = stock - #{num} WHERE sku_id = #{id} AND stock >= #{num}")
    int decreaseStock(@Param("id") Long id, @Param("num") Integer num);

    //@Update("UPDATE tb_stock SET  seckill_stock = seckill_stock-1 WHERE sku_id =#{skuId}  AND  seckill_stock>0;")
    @Update("UPDATE tb_stock SET seckill_stock = seckill_stock-1,version = version +1 WHERE sku_id =#{skuId} AND version =#{version} AND seckill_stock>0; ")
    int updateStock(@Param("skuId") Long id ,@Param("version") Long version);

}
