package com.leyou.page.client;

import com.leyou.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * ClassName: SpecificationClient <br/>
 * Description: TODO
 * Date 2020/5/3 10:23
 *
 * @author Lenovo
 **/
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
