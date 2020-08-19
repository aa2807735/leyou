package com.leyou.search.client;

import com.leyou.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * ClassName: BrandClient <br/>
 * Description: TODO
 * Date 2020/5/3 10:24
 *
 * @author Lenovo
 **/
@FeignClient("item-service")
public interface BrandClient  extends BrandApi {

}
