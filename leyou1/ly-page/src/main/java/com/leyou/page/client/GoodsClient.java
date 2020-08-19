package com.leyou.page.client;

import com.leyou.api.GoodApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * ClassName: GoodsClient <br/>
 * Description: TODO
 * Date 2020/5/3 9:59
 *
 * @author Lenovo
 **/
@FeignClient("item-service")
public interface GoodsClient extends GoodApi {
}
