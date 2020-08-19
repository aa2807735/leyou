package com.leyou.order.client;

import com.leyou.api.GoodApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * ClassName: GoodsClient <br/>
 * Description: TODO
 * Date 2020/5/10 14:07
 *
 * @author Lenovo
 **/
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodApi {
}
