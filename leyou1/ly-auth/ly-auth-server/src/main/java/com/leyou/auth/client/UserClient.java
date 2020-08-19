package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * ClassName: UserClient <br/>
 * Description: TODO
 * Date 2020/5/8 13:32
 *
 * @author Lenovo
 **/
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
